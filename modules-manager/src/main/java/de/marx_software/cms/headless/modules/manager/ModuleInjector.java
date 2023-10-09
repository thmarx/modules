package de.marx_software.cms.headless.modules.manager;

import de.marx_software.cms.headless.modules.api.ExtensionPoint;



/**
 *
 * @author marx
 */
public interface ModuleInjector {
	
	public void inject (final ExtensionPoint extension);
}
