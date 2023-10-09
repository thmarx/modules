package de.marx_software.cms.headless.modules.api;


/**
 * The Context is passed to the module. It can be used to inject implementation dependend objects or functionality
 *
 * @author marx
 */
public abstract class Context {

	private ServiceRegistry services = null;
	
	public ServiceRegistry serviceRegistry() {
		return services;
	}

	public void setServices(ServiceRegistry services) {
		this.services = services;
	}
	
	
}
