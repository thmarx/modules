package com.github.thmarx.modules.api;

/*-
 * #%L
 * modules-api
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
import java.net.URI;
import java.util.List;

/**
 *
 * @author marx
 */
public interface ModuleManager extends AutoCloseable {

	/**
	 * activates a module.
	 *
	 * @param moduleId
	 * @return returns true if the module is correctly or allready installed, otherwise false
	 * @throws java.io.IOException
	 */
	boolean activateModule(final String moduleId) throws IOException;
	
	/**
	 *
	 * @param moduleId
	 * @return
	 */
	boolean deactivateModule(final String moduleId) throws IOException;

	/**
	 * Returns the module description.
	 * @param id
	 * @return
	 * @throws IOException
	 */
	ModuleDescription description(final String id) throws IOException;

	/**
	 * Returns all Extensions of the given type.
	 *
	 * @param <T>
	 * @param extensionClass
	 * @return
	 */
	<T extends ExtensionPoint> List<T> extensions(Class<T> extensionClass);

	/**
	 * install a new module.
	 *
	 * @param moduleURI
	 * @return the id of the newly installed module.
	 * @throws IOException
	 */
	String installModule(final URI moduleURI) throws IOException;

	/**
	 * uninstall module,
	 *
	 * @param moduleId the ID of the module
	 * @param deleteData should the data directory of the module be deleted too.
	 * @return
	 * @throws IOException
	 */
	boolean uninstallModule(final String moduleId, final boolean deleteData) throws IOException;
	
	public Module module(final String id);
	
	/**
	 * Returns the configuration of the module manager.
	 *
	 * @return
	 */
	public ManagerConfiguration configuration();
	
	/**
	 * Returns a list of all available module ids.
	 * 
	 * @return 
	 */
	public List<String> getModuleIds ();
	
	public ServiceRegistry getServiceRegistry();
}
