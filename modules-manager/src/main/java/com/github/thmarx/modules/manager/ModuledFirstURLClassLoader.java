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
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author marx
 */
public class ModuledFirstURLClassLoader extends URLClassLoader {

	public ModuledFirstURLClassLoader(URL[] classpath, ClassLoader parent) {
		super(classpath, parent);
	}

	@Override
	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		// First, check if the class has already been loaded
		Class<?> c = findLoadedClass(name);
		if (c == null) {
			
			if (c == null) {
				try {
					// checking local
					c = findClass(name);
				} catch (ClassNotFoundException e) {
					// checking parent
					// This call to loadClass may eventually call findClass again, in case the parent doesn't find anything.
					c = super.loadClass(name, resolve);
				}
			}
		}
		if (resolve) {
			resolveClass(c);
		}
		return c;
	}

	@Override
	public URL getResource(String name) {
		URL url = null;
		if (url == null) {
			url = findResource(name);
			if (url == null) {
				// This call to getResource may eventually call findResource again, in case the parent doesn't find anything.
				url = super.getResource(name);
			}
		}
		return url;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		/**
		 * Similar to super, but local resources are enumerated before parent
		 * resources
		 */
		Enumeration<URL> localUrls = findResources(name);
		Enumeration<URL> parentUrls = null;
		if (getParent() != null) {
			parentUrls = getParent().getResources(name);
		}
		final List<URL> urls = new ArrayList<>();
		if (localUrls != null) {
			while (localUrls.hasMoreElements()) {
				urls.add(localUrls.nextElement());
			}
		}
		if (parentUrls != null) {
			while (parentUrls.hasMoreElements()) {
				urls.add(parentUrls.nextElement());
			}
		}
		return new Enumeration<URL>() {
			Iterator<URL> iter = urls.iterator();

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
	public InputStream getResourceAsStream(String name) {
		URL url = getResource(name);
		try {
			return url != null ? url.openStream() : null;
		} catch (IOException e) {
		}
		return null;
	}

}
