package de.marx_software.cms.headless.modules.api;




/**
 *
 * @author marx
 */
public abstract class ModuleLifeCycleExtension extends BaseExtension {
	/** 
	 * Called when the module is activated.
	 */
	public void activate () {};
	/**
	 * called when the module is deactivated.
	 */
	public void deactivate () {};
}
