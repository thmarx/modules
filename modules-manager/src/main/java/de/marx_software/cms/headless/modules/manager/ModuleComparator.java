package de.marx_software.cms.headless.modules.manager;


import java.util.Comparator;

/**
 *
 * @author marx
 */
public class ModuleComparator implements Comparator<ModuleImpl> {

	@Override
	public int compare(ModuleImpl o1, ModuleImpl o2) {
		return ((Integer) o1.getDependencies().size()).compareTo(o2.getDependencies().size());
	}
	
}
