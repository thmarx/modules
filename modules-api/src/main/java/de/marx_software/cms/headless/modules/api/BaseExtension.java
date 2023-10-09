package de.marx_software.cms.headless.modules.api;


/**
 *
 * @author marx
 * @param <C>
 */
public abstract class BaseExtension<C extends Context> implements ExtensionPoint<C> {

	protected ModuleConfiguration configuration;
	
	private C context;

	@Override
	public void setContext(C context) {
		this.context = context;
	}
	
	public C getContext () {
		return this.context;
	}
	
	@Override
	public void setConfiguration(ModuleConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
}
