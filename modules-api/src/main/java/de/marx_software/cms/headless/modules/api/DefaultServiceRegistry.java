package de.marx_software.cms.headless.modules.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author thmarx
 */
public final class DefaultServiceRegistry implements ServiceRegistry {

	private final JdkCopyOnWriteArrayListMultiMap<String, Object> services = new JdkCopyOnWriteArrayListMultiMap<>();

	public DefaultServiceRegistry() {
	}

	/**
	 * Registers a new sevice implementation.
	 *
	 * @param <T> The service type.
	 * @param clazz The service interface.
	 * @param object The service implementation.
	 */
	@Override
	public <T> void register(Class<T> clazz, T object) {
		services.put(clazz.getCanonicalName(), object);
	}

	/**
	 * Removes a service implementation.
	 *
	 * @param <T> The service type.
	 * @param clazz The service class.
	 * @param object The service.
	 */
	@Override
	public <T> void unregister(Class<T> clazz, T object) {
		services.remove(clazz.getCanonicalName(), object);
	}

	/**
	 * Check if a service for the interface is registered.
	 *
	 * @param clazz The desired service interface.
	 * @return
	 */
	@Override
	public boolean exists(Class clazz) {
		return services.exists(clazz.getCanonicalName());
	}

	/**
	 * Returns the first registered service instance of null.
	 *
	 * @param <T> The service type
	 * @param clazz the service interface
	 * @return the first registered implementation or null.
	 */
	@Override
	public <T> Optional<T> single(Class<T> clazz) {

		T service = null;
		if (services.exists(clazz.getCanonicalName())) {
			List<Object> value = services.get(clazz.getCanonicalName());
//			Object serviceInstance = proxy(value.get(0), value.get(0).getClass().getClassLoader(), clazz.getClassLoader(), clazz);
//			service = (T) serviceInstance;
			service = (T) value.get(0);
		}

		return Optional.ofNullable(service);
	}

	/**
	 * Returns all implementations of the desired service.
	 *
	 * @param <T> The service type.
	 * @param clazz The service interface
	 * @return A list with all service implementations. If no implementation is
	 * registered, an empty list is returned.
	 */
	@Override
	public <T> List<T> get(Class<T> clazz) {

		List<T> result = new ArrayList<>();
		if (services.exists(clazz.getCanonicalName())) {
			for (Object value : services.get(clazz.getCanonicalName())) {
				Object serviceInstance = proxy(value, value.getClass().getClassLoader(), clazz.getClassLoader(), clazz);
				result.add((T) serviceInstance);
			}
		}

		return result;
	}

	private static class ServiceInvocationHandler implements InvocationHandler {

		final Object service;

		public ServiceInvocationHandler(final Object service) {
			this.service = service;
		}

		@Override
		public Object invoke(Object o, Method method, Object[] os) throws Throwable {

			Method m = service.getClass().getMethod(method.getName(), method.getParameterTypes());
			Object obj = m.invoke(service, os);

			if (obj.getClass().getInterfaces() == null || obj.getClass().getInterfaces().length == 0) {
				return obj;
			}

			ServiceInvocationHandler handler = new ServiceInvocationHandler(obj);

			return Proxy.newProxyInstance(getClass().getClassLoader(), obj.getClass().getInterfaces(), handler);
		}

	}

	/*
	 * creates a proxy for an object obj loaded in sourceLoader hierarchy 
	 * that is visible in destLoader as destClass interface
	 * assuming all methods of destClass are implemented in obj
	 * exactly with same signature
	 */
	Object proxy(final Object obj,
			final ClassLoader sourceLoader,
			final ClassLoader destLoader,
			final Class<?> destClass) {

		return Proxy.newProxyInstance(destLoader, new Class<?>[]{destClass},
				(proxy, method, args)
				-> threadClassLoader(sourceLoader, () -> {
					final Class<?>[] mappedArgTypes = new Class<?>[args == null ? 0 : args.length];
					final Object[] mappedArgs = new Object[mappedArgTypes.length];
					final Class<?>[] sourceTypes = method.getParameterTypes();

					for (int i = 0; args != null && i < mappedArgTypes.length; i++) {
						if (sourceTypes[i].getClassLoader() == null) {
							mappedArgTypes[i] = sourceTypes[i];
							mappedArgs[i] = args[i];
						} else {
							mappedArgTypes[i] = sourceLoader.loadClass(sourceTypes[i].getName());
							mappedArgs[i] = proxy(args[i], destLoader, sourceLoader, mappedArgTypes[i]);
						}
					}

					final Method realMethod = obj.getClass().getMethod(method.getName(), mappedArgTypes);
					final Object result = realMethod.invoke(obj, mappedArgs);
					if (method.getReturnType().getClassLoader() == null) {
						return result;
					}
					return proxy(result, sourceLoader, destLoader, method.getReturnType());
				}));
	}

	private Object threadClassLoader(final ClassLoader classLoader, final Callable<Object> callable) throws Exception {
		final ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(classLoader);
			return callable.call();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}
		return null;
	}

	private class JdkCopyOnWriteArrayListMultiMap<K, V> {

		private final ConcurrentMap<K, List<V>> cache = new ConcurrentHashMap<>();

		public boolean exists(K k) {
			return cache.containsKey(k);
		}

		public List<V> get(K k) {
			return cache.get(k);
		}

		public List<V> remove(K k) {
			return cache.remove(k);
		}

		public void put(K k, V v) {
			List<V> list = cache.get(k);
			if (list == null) {
				list = new CopyOnWriteArrayList<>();
				List<V> oldList = cache.putIfAbsent(k, list);
				if (oldList != null) {
					list = oldList;
				}
			}
			list.add(v);
		}

		public boolean remove(K k, V v) {
			List<V> list = cache.get(k);
			if (list == null) {
				return false;
			}
			if (list.isEmpty()) {
				cache.remove(k);
				return false;
			}
			boolean removed = list.remove(v);
			if (removed && list.isEmpty()) {
				cache.remove(k);
			}
			return removed;
		}
	}
}
