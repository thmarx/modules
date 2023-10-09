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

import java.util.List;

/**
 *
 * @author marx
 */
public interface Module {

	public enum Priority {
		NORMAL,
		// System modules must be loaded before default modules
		HIGH,
		HIGHER,
		HIGHEST;
	}
	
	Priority getPriority ();

	<T extends ExtensionPoint> List<T> extensions(Class<T> extensionClass);

	String getAuthor();

	String getDescription();

	String getId();

	String getName();

	String getVersion();

	boolean provides(Class<? extends ExtensionPoint> extensionClass);

}
