package de.marx_software.cms.headless.modules.manager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author marx
 */
public class ModuleAPIClassLoader extends ClassLoader {

	private final List<String> apiPackages;

	private final URLClassLoader parent;

	public ModuleAPIClassLoader(URLClassLoader classLoader, List<String> apiPackages) {
		super(classLoader);

		this.parent = classLoader;
		List<String> temp = apiPackages != null ? apiPackages : new ArrayList<>();
		this.apiPackages = temp.stream().map(c -> !c.endsWith(".") ? c + "." : c).collect(Collectors.toList());

		this.apiPackages.add("de.marx_software.cms.headless.modules.api.");
		this.apiPackages.add("java.");
		this.apiPackages.add("javax.");
		this.apiPackages.add("com.sun.");
		this.apiPackages.add("sun.");
	}

	private boolean isAllowed(final String name) {
		for (String packageName : apiPackages) {
			if (name.startsWith(packageName)) {
				return true;
			}

			String tempname = packageName.replaceAll("\\.", "/");
			if (name.startsWith("/")) {
				tempname = "/" + tempname;
			}
			if (name.startsWith(tempname)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
		if (isAllowed(className)) {
			return getParent().loadClass(className);
		}

		throw new ClassNotFoundException(className + "not visible");
	}

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException {
			if (isAllowed(className)) {
				return getParent().loadClass(className);
			}

		throw new ClassNotFoundException(className + "not visible");
	}

	@Override
	protected Class<?> findClass(String className) throws ClassNotFoundException {
		if (isAllowed(className)) {
			return getParent().loadClass(className);
		}

		throw new ClassNotFoundException(className + "not visible");
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if (isAllowed(name)) {
			return getParent().getResourceAsStream(name);
		}

		return null;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (isAllowed(name)) {
			return getParent().getResources(name);
		}

		return new Enumeration<URL>() {
			Iterator<URL> iter = Collections.EMPTY_LIST.iterator();

			@Override
			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			@Override
			public URL nextElement() {
				return iter.next();
			}
		};
	}

	@Override
	public URL getResource(String name) {
		if (isAllowed(name)) {
			return getParent().getResource(name);
		}
		return null;
	}

	@Override
	protected Enumeration<URL> findResources(String name) throws IOException {
		if (isAllowed(name)) {
			return parent.findResources(name);
		}

		return new Enumeration<URL>() {
			Iterator<URL> iter = Collections.EMPTY_LIST.iterator();

			@Override
			public boolean hasMoreElements() {
				return iter.hasNext();
			}

			@Override
			public URL nextElement() {
				return iter.next();
			}
		};
	}

	@Override
	protected URL findResource(String name) {
		if (isAllowed(name)) {
			return parent.findResource(name);
		}
		return null;
	}

}
