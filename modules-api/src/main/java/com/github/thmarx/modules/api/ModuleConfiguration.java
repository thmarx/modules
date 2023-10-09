package com.github.thmarx.modules.api;

/*-
 * #%L
 * modules-api
 * %%
 * Copyright (C) 2023 Thorsten Marx
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */



import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author thmarx
 */
public class ModuleConfiguration {

	private final Properties properties = new Properties();
	
	final File configFile;
	
	final File dataDirectory;
	
	public ModuleConfiguration (final File path) throws IOException{
		configFile = new File(path, "configuration.properties");
		if (!configFile.exists()) {
			configFile.createNewFile();
		}
		properties.load(new FileReader(configFile));
		
		this.dataDirectory = new File(path.toURI());
	}
	
	public File getDataDir () {
		return dataDirectory;
	}
	
	
	/**
	 * Stores the module configuration in the data directory for this module.
	 * 
	 * @throws IOException 
	 */
	public void store () throws IOException {
		properties.store(new FileWriter(configFile), "module configuraion saved");
	}
	/**
	 * Returns a configuration property value.
	 * 
	 * @param key The key of the property.
	 * @param defaultValue The default value to return.
	 * @return The value for the key or the default value.
	 */
	public Object get (final String key, final Object defaultValue) {
		return properties.getOrDefault(key, defaultValue);
	}
	/**
	 * Returns a configuration property value as int.
	 * 
	 * @param key The key of the property.
	 * @param defaultValue The default value to return.
	 * @return The value for the key or the default value.
	 */
	public int getInt (final String key, final int defaultValue) {
		Object value = properties.get(key);
		if (value != null) {
			if (value instanceof Integer) {
				return (int) value;
			} else {
				return Integer.parseInt(((String)value).trim());
			}
		}
		return defaultValue;
	}
	/**
	 * Returns a configuration property value as String.
	 * 
	 * @param key The key of the property.
	 * @param defaultValue The default value to return.
	 * @return The value for the key or the default value.
	 */
	public String getString (final String key, final String defaultValue) {
		Object value = properties.get(key);
		if (value != null) {
			if (value instanceof String) {
				return (String) value;
			} else {
				return String.valueOf(value);
			}
		}
		return defaultValue;
	}
	
	/**
	 * Sets a configuration property.
	 * 
	 * @param key The property key.
	 * @param value The property value. 
	 */
	public void set (final String key, final Object value) {
		properties.put(key, value);
	}
}
