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
import java.util.Optional;

/**
 *
 * @author marx
 */
public interface ServiceRegistry {

	/**
	 * Check if a service for the interface is registered.
	 *
	 * @param clazz The desired service interface.
	 * @return
	 */
	boolean exists(Class clazz);

	/**
	 * Returns all implementations of the desired service.
	 *
	 * @param <T> The service type.
	 * @param clazz The service interface
	 * @return A list with all service implementations. If no implementation is
	 * registered, an empty list is returned.
	 */
	<T> List<T> get(Class<T> clazz);

	/**
	 * Registers a new sevice implementation.
	 *
	 * @param <T> The service type.
	 * @param clazz The service interface.
	 * @param object The service implementation.
	 */
	<T> void register(Class<T> clazz, T object);

	/**
	 * Returns the first registered service instance of null.
	 *
	 * @param <T> The service type
	 * @param clazz the service interface
	 * @return the first registered implementation or null.
	 */
	<T> Optional<T> single(Class<T> clazz);

	/**
	 * Removes a service implementation.
	 *
	 * @param <T> The service type.
	 * @param clazz The service class.
	 * @param object The service.
	 */
	<T> void unregister(Class<T> clazz, T object);
	
}
