package de.marx_software.cms.headless.modules.api;


/**
 *
 * @author marx
 */
public class ModuleDescription {
	private String name;
	private String description;
	private String version;
	
	public ModuleDescription () {
		
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
        
        
}
