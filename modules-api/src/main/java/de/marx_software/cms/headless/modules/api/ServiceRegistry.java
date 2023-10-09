package de.marx_software.cms.headless.modules.api;


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
