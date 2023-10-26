package com.github.thmarx.modules.manager;

/*-
 * #%L
 * modules-manager
 * %%
 * Copyright (C) 2023 Thorsten Marx
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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

	private final ClassLoader parent;

	public ModuleAPIClassLoader(ClassLoader classLoader, List<String> apiPackages) {
		super(classLoader);

		this.parent = classLoader;
		List<String> temp = apiPackages != null ? apiPackages : new ArrayList<>();
		this.apiPackages = temp.stream().map(c -> !c.endsWith(".") ? c + "." : c).collect(Collectors.toList());

		this.apiPackages.add("com.github.thmarx.modules.api.");
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
			return parent.getResources(name);
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
			return parent.getResource(name);
		}
		return null;
	}

}
