package de.marx_software.cms.headless.modules.api;




/**
 *
 * @author marx
 * @param <C>
 */
public interface ExtensionPoint<C extends Context> {
	void setConfiguration (ModuleConfiguration configuration);
	
	void setContext (C context);
	
	void init ();
}
