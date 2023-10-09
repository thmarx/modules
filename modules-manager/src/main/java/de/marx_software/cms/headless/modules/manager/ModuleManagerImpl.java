package de.marx_software.cms.headless.modules.manager;

import de.marx_software.cms.headless.modules.api.Context;
import de.marx_software.cms.headless.modules.api.Module;
import de.marx_software.cms.headless.modules.api.DefaultServiceRegistry;
import de.marx_software.cms.headless.modules.api.ExtensionPoint;
import de.marx_software.cms.headless.modules.api.ManagerConfiguration;
import de.marx_software.cms.headless.modules.api.ModuleDescription;
import de.marx_software.cms.headless.modules.api.ModuleLifeCycleExtension;
import de.marx_software.cms.headless.modules.api.ModuleManager;
import de.marx_software.cms.headless.modules.api.ServiceRegistry;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.net.URI;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ModuleManager loads all modules from a given directoy.
 *
 * @author thmarx
 */
public class ModuleManagerImpl implements ModuleManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleManagerImpl.class);

	protected static final String MODULE_CONFIGURATION_FILE = "moduleconfiguration.json";
	
	public static class Builder {
		private File path = null;
		private Context context = null;
		private ModuleAPIClassLoader classLoader = null;
		private ServiceRegistry serviceRegistry = null;
		private ModuleInjector injector = null;
		
		
		
		public ModuleManager build () {
			return new ModuleManagerImpl(this);
		}

		public Builder setPath(File path) {
			this.path = path;
			return this;
		}

		public Builder setContext(Context context) {
			this.context = context;
			return this;
		}

		public Builder setClassLoader(ModuleAPIClassLoader classLoader) {
			this.classLoader = classLoader;
			return this;
		}

		public Builder setServiceRegistry(ServiceRegistry serviceRegistry) {
			this.serviceRegistry = serviceRegistry;
			return this;
		}

		public Builder setInjector(ModuleInjector injector) {
			this.injector = injector;
			return this;
		}
	}
	
	public static Builder builder () {
		return new Builder();
	}
	

	/**
	 * Creates a new ModuleManager instance.
	 *
	 * @param path the path where the installed modules and the modules data
	 * directory is located.
	 * @param context the context
	 * @return A new ModuleManager.
	 */
	public static ModuleManager create(final File path, final Context context) {
		return builder().setClassLoader(new ModuleAPIClassLoader((URLClassLoader) ModuleManagerImpl.class.getClassLoader(), Collections.EMPTY_LIST)).setPath(path).setContext(context).setInjector(null).build();
	}

	/**
	 * Creates a ne ModuleManager instance.
	 *
	 * @param path the path where the installed modules and the modules data
	 * directory is located.
	 * @param context the context
	 * @param classLoader the classloader
	 * @return
	 */
	public static ModuleManager create(final File path, final Context context, final ModuleAPIClassLoader classLoader) {
		return builder().setClassLoader(classLoader).setPath(path).setContext(context).setInjector(null).build();
	}

	/**
	 * Creates a new ModuleManager instance.
	 *
	 * @param path the path where the installed modules and the modules data
	 * directory is located.
	 * @param context the context
	 * @param injector Injector for dependency injection
	 * @return A new ModuleManager.
	 */
	public static ModuleManager create(final File path, final Context context, final ModuleInjector injector) {
		return builder().setClassLoader(new ModuleAPIClassLoader((URLClassLoader) ModuleManagerImpl.class.getClassLoader(), Collections.EMPTY_LIST)).setPath(path).setContext(context).setInjector(injector).build();
	}

	/**
	 * Creates a ne ModuleManager instance.
	 *
	 * @param path the path where the installed modules and the modules data
	 * directory is located.
	 * @param context the context
	 * @param classLoader the classloader
	 * @param injector Injector for dependency injection
	 * @return
	 */
	public static ModuleManager create(final File path, final Context context, final ModuleAPIClassLoader classLoader, final ModuleInjector injector) {
		return builder().setClassLoader(classLoader).setPath(path).setContext(context).setInjector(injector).build();
	}

	final File path;

	final ModuleLoader moduleLoader;

	final ModuleAPIClassLoader globalClassLoader;

	private ManagerConfiguration configuration;

	private final Context context;

	final ModuleInjector injector;

	public ModuleManagerImpl() {
		this.path = null;
		this.globalClassLoader = null;
		this.moduleLoader = null;
		this.context = null;
		this.injector = null;
	}

	private ModuleManagerImpl(final Builder builder) {
		this.path = builder.path;
		this.context = builder.context;
		this.injector = builder.injector;

		if (builder.serviceRegistry != null) {
			this.context.setServices(builder.serviceRegistry);
		} else {
			this.context.setServices(new DefaultServiceRegistry());
		}
		
		File config = new File(path, MODULE_CONFIGURATION_FILE);
		if (config.exists()) {
			this.configuration = ManagerConfiguration.load(config);
		} else {
			this.configuration = new ManagerConfiguration();
		}
		this.globalClassLoader = builder.classLoader;
		this.moduleLoader = new ModuleLoader(configuration, path, this.globalClassLoader, this.context, this.injector);

		File[] moduleFiles = new File(path, "modules").listFiles((File file) -> file.isDirectory());
		File moduleData = new File(path, "modules_data");

		Set<String> allUsedModuleIDs = new HashSet<>();

		Map<String, ModuleImpl> modules = new HashMap<>();
		if (moduleFiles != null) {
			loadModules(moduleFiles, moduleData, allUsedModuleIDs, modules);
		}
		configuration.getModules().values().stream().filter((mc) -> (!allUsedModuleIDs.contains(mc.getId()))).forEach((mc) -> {
			configuration.remove(mc.getId());
		});

		List<ModuleImpl> moduleList = new ArrayList<>(modules.values());
		moduleLoader.tryToLoadModules(moduleList);
		for (ModuleImpl module : moduleList) {
			if (configuration.get(module.getId()).isActive()) {
				LOGGER.warn("could not load module: " + module.getName());
				configuration.get(module.getId()).setActive(false);
			}
		}

		moduleLoader.activeModules().values().forEach((mod) -> {
			configuration.get(mod.getId()).setActive(true);
			try {
				mod.init(this.globalClassLoader);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		});

		activateActiveModulesByPriority(ModuleImpl.Priority.HIGHEST);
		activateActiveModulesByPriority(ModuleImpl.Priority.HIGHER);
		activateActiveModulesByPriority(ModuleImpl.Priority.HIGH);
		activateActiveModulesByPriority(ModuleImpl.Priority.NORMAL);

		configuration.getModules().values().forEach((mc) -> {
			if (!moduleLoader.activeModules().containsKey(mc.getId())) {
				configuration.get(mc.getId()).setActive(false);
			}
		});
		saveConfiguration();
	}

	public ServiceRegistry getServiceRegistry() {
		return context.serviceRegistry();
	}

	private void activateActiveModulesByPriority(final ModuleImpl.Priority priority) {
		moduleLoader.activeModules().values().stream().filter((mod) -> priority.equals(mod.getPriority())).forEach((mod) -> {
			mod.extensions(ModuleLifeCycleExtension.class).forEach((mle) -> {
				mle.setContext(context);
				mle.activate();
			});
		});
	}

	private void loadModules(File[] moduleFiles, File moduleData, Set<String> allUsedModuleIDs, Map<String, ModuleImpl> modules) {
		for (File module : moduleFiles) {
			try {
				ModuleImpl mod = new ModuleImpl(module, moduleData, this.context, this.injector);
				allUsedModuleIDs.add(mod.getId());
				modules.put(mod.getId(), mod);
				if (configuration.get(mod.getId()) == null) {
					configuration.add(new ManagerConfiguration.ModuleConfig(mod.getId()).setModuleDir(module.getName()));
				}
			} catch (IOException ex) {
				LOGGER.error("", ex);
				// deactivate module
				String modid = module.getName();
				allUsedModuleIDs.add(modid);
				if (configuration.get(modid) != null) {
					LOGGER.warn("deactivate module caused by an error");
					configuration.get(modid).setActive(false);
				}
			}
		}
	}

	@Override
	public void close() {
		extensions(ModuleLifeCycleExtension.class).stream().forEach((ModuleLifeCycleExtension mle) -> {
			mle.setContext(context);
			mle.deactivate();
		});
		saveConfiguration();
	}

	private void saveConfiguration() {
		File config = new File(path, MODULE_CONFIGURATION_FILE);
		ManagerConfiguration.store(config, this.configuration);
	}

	/**
	 * Returns a module by id. All the modules are loaded correctly so you can
	 * get extensions.
	 *
	 * @param id The id of the module.
	 * @return The module for the given id or null.
	 */
	@Override
	public Module module(final String id) {
		return moduleLoader.activeModules.get(id);
	}

	@Override
	public List<String> getModuleIds() {
		List<ManagerConfiguration.ModuleConfig> modules = new ArrayList<>(configuration.getModules().values());

		return modules.stream().map((mc) -> {
			try {
				File moduleDir = new File(path, "modules/" + configuration.get(mc.getId()).getModuleDir());
				File moduleData = new File(path, "modules_data");
				ModuleImpl module = new ModuleImpl(moduleDir, moduleData, this.context, this.injector);
				return module;
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}).sorted(new ModuleComparator()).map(Module::getId).collect(Collectors.toList());
	}

	/**
	 * Returns the module description.
	 *
	 * @param id
	 * @return
	 * @throws IOException
	 */
	@Override
	public ModuleDescription description(final String id) throws IOException {
		ModuleImpl module;
		if (moduleLoader.activeModules.containsKey(id)) {
			module = moduleLoader.activeModules.get(id);
		} else {
			ManagerConfiguration.ModuleConfig mc = configuration.get(id);
			File moduleDir = new File(path, "modules/" + configuration.get(mc.getId()).getModuleDir());
			module = new ModuleImpl(moduleDir, null, this.context, this.injector);
		}

		ModuleDescription description = new ModuleDescription();
		description.setVersion(module.getVersion());
		description.setName(module.getName());
		description.setDescription(module.getDescription());
		return description;
	}

	/**
	 * activates a module.
	 *
	 * @param moduleId
	 * @return returns true if the module is correctly or allready installed,
	 * otherwise false
	 * @throws java.io.IOException
	 */
	@Override
	public boolean activateModule(final String moduleId) throws IOException {
		try {
			if (configuration.get(moduleId) == null || !configuration.get(moduleId).isActive()) {
				return moduleLoader.activateModule(moduleId);
			} else if (configuration.get(moduleId) != null && configuration.get(moduleId).isActive()) {
				return true;
			}
			return false;
		} finally {
			saveConfiguration();
		}
	}

	/**
	 *
	 * @param moduleId
	 * @return
	 */
	@Override
	public boolean deactivateModule(final String moduleId) throws IOException {
		try {
			if (configuration.get(moduleId) == null) {
				return true;
			} else if (configuration.get(moduleId) != null && !configuration.get(moduleId).isActive()) {
				return true;
			}

			moduleLoader.deactivateModule(moduleId);

			configuration.get(moduleId).setActive(false);
			return true;
		} finally {
			saveConfiguration();
		}
	}

	/**
	 * install a new module.
	 *
	 * @param moduleURI
	 * @return the id of the newly installed module.
	 * @throws IOException
	 */
	@Override
	public String installModule(final URI moduleURI) throws IOException {

		Path tempDirectory = Files.createTempDirectory("modules");
		File moduleTempDir = ModulePacker.unpackArchive(new File(moduleURI), tempDirectory.toFile());
		File moduleData = new File(path, "modules_data");
		ModuleImpl tempModule = new ModuleImpl(moduleTempDir, moduleData, this.context, this.injector);
		if (getModuleIds().contains(tempModule.getId())) {
			deactivateModule(tempModule.getId());
			uninstallModule(tempModule.getId(), false);
		}
		ModulePacker.moveDirectoy(moduleTempDir, new File(this.path, "modules/" + moduleTempDir.getName()));
		File moduleDir = new File(this.path, "modules/" + moduleTempDir.getName());

		ModuleImpl module = new ModuleImpl(moduleDir, moduleData, this.context, this.injector);

		ManagerConfiguration.ModuleConfig config = configuration.get(module.getId());
		if (config == null) {
			config = new ManagerConfiguration.ModuleConfig(module.getId()).setModuleDir(moduleDir.getName());
		}
		config.setActive(false);
		configuration.add(config);

		saveConfiguration();

		return module.getId();
	}

	/**
	 * uninstall module,
	 *
	 * @param moduleId the ID of the module
	 * @param deleteData should the data directory of the module be deleted too.
	 * @return
	 * @throws IOException
	 */
	@Override
	public boolean uninstallModule(final String moduleId, final boolean deleteData) throws IOException {
		if (configuration.get(moduleId) != null && configuration.get(moduleId).isActive()) {
			throw new IOException("module must be deactivated first");
		} else if (configuration.get(moduleId) == null) {
			return true;
		}

		configuration.remove(moduleId);
		saveConfiguration();

		boolean deleted = ModulePacker.deleteDirectory(new File(path, "modules/" + moduleId));

		File moduleData = new File(path, "modules_data/" + moduleId);
		if (deleteData && deleted && moduleData.exists()) {
			deleted = ModulePacker.deleteDirectory(moduleData);
		}

		return deleted;
	}

	/**
	 * Returns all Extensions of the given type.
	 *
	 * @param <T>
	 * @param extensionClass
	 * @return
	 */
	@Override
	public <T extends ExtensionPoint> List<T> extensions(Class<T> extensionClass) {
		List<T> extensions = new ArrayList<>();
		moduleLoader.activeModules().values().forEach((ModuleImpl m) -> {
			if (m.provides(extensionClass)) {
				extensions.addAll(m.extensions(extensionClass));
			}
		});
		return extensions;
	}

	/**
	 * Returns the configuration of the module manager.
	 *
	 * @return
	 */
	@Override
	public ManagerConfiguration configuration() {
		return configuration;
	}
}