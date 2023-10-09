package de.marx_software.cms.headless.modules.manager;


import de.marx_software.cms.headless.modules.api.Context;
import de.marx_software.cms.headless.modules.api.ManagerConfiguration;
import de.marx_software.cms.headless.modules.api.ModuleLifeCycleExtension;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author marx
 */
public class ModuleLoader {

	private final ManagerConfiguration configuration;

	final Map<String, ModuleImpl> activeModules = new ConcurrentHashMap<>();

	final File path;

	final ClassLoader globalClassLoader;

	final Context context;
	final ModuleInjector injector;

	protected ModuleLoader(final ManagerConfiguration configuration, final File path, final ClassLoader globalClassLoader, final Context context, final ModuleInjector injector) {
		this.configuration = configuration;
		this.path = path;
		this.globalClassLoader = globalClassLoader;
		this.context = context;
		this.injector = injector;
	}

	protected Map<String, ModuleImpl> activeModules() {
		return activeModules;
	}

	protected boolean deactivateModule(final String moduleId) throws IOException {

		ModuleImpl module = activeModules().get(moduleId);
		module.extensions(ModuleLifeCycleExtension.class).stream().forEach((ModuleLifeCycleExtension mle) -> {
			mle.setContext(context);
			mle.deactivate();
		});

		activeModules().get(moduleId).close();
		activeModules().remove(moduleId);

		return true;
	}

	protected boolean activateModule(final String moduleId) throws IOException {
		
		File moduleDir = new File(path, "modules/" + configuration.get(moduleId).getModuleDir());
		File moduleData = new File(path, "modules_data");
//		File moduleData = activeModules().get(moduleId).getModulesDataDir();

		ModuleImpl module = new ModuleImpl(moduleDir, moduleData, this.context, this.injector);

		if (areDependencyFulfilled(module)) {
			ManagerConfiguration.ModuleConfig config = configuration.get(moduleId);
			if (config == null) {
				config = new ManagerConfiguration.ModuleConfig(moduleId);
			}

			module.init(this.globalClassLoader);

			config.setActive(true);
			module.extensions(ModuleLifeCycleExtension.class).stream().forEach((ModuleLifeCycleExtension mle) -> {
				mle.setContext(context);
				mle.activate();
			});
			configuration.add(config);

			activeModules().put(module.getId(), module);
			return true;
		}
		return false;
	}
	
	protected void tryToLoadModules(final List<ModuleImpl> modules) {
		// sort modules by dependency count low to max
		Collections.sort(modules, new ModuleComparator());

		int tryCount = 0;
		int oldSize = modules.size();
		while (!modules.isEmpty()) {
			if (tryCount > 2) {
				break;
			}
			loadFulfilledModules(modules);
			if (modules.size() == oldSize) {
				tryCount++;
			} else {
				oldSize = modules.size();
			}
		}
	}

	private void loadFulfilledModules(final List<ModuleImpl> modules) {
		for (final ModuleImpl module : modules) {
			if (areDependencyFulfilled(module) && configuration.get(module.getId()).isActive()) {
				activeModules.put(module.getId(), module);
			}
		}
		modules.removeAll(activeModules.values());
	}

	private boolean areDependencyFulfilled(final ModuleImpl module) {
		return module.getDependencies().stream().noneMatch((dependency) -> (!activeModules.containsKey(dependency.id())));
	}
}
