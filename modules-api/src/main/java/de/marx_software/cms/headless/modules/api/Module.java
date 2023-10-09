package de.marx_software.cms.headless.modules.api;

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
