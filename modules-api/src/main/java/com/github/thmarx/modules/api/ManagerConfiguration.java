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


import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author thmarx
 */
public class ManagerConfiguration {
	
	private ConcurrentMap<String, ModuleConfig> modules = new ConcurrentHashMap<>();

	public ManagerConfiguration () {
		
	}

	public ConcurrentMap<String, ModuleConfig> getModules() {
		return modules;
	}

	public void setModules(ConcurrentMap<String, ModuleConfig> modules) {
		this.modules = modules;
	}
	
	public ModuleConfig get (final String moduleId) {
		return modules.get(moduleId);
	}
	public void add (final ModuleConfig config) {
		modules.put(config.getId(), config);
	}
	public void remove (final String moduleId) {
		modules.remove(moduleId);
	}
	

	public static void store(final File file, final ManagerConfiguration config) {
		
        final String yamlConfiguration = new Gson().toJson(config);
		
		try (BufferedWriter buffer = Files.newBufferedWriter(Paths.get(file.toURI()), Charset.forName("UTF-8"))){
			buffer.write(yamlConfiguration);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		
	}
	
	public static ManagerConfiguration load (final File file) {
		try {
			return new Gson().fromJson(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")), ManagerConfiguration.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	
	
	public static class ModuleConfig {
		
		private boolean active = false;
		
		private String id;
		
		private String moduleDir;
		
		private String moduleDataDir;
		
		public ModuleConfig () {
			
		}
		public ModuleConfig (final String id) {
			this.id = id;
		}

		public void setId (final String id) {
			this.id = id;
		}
		
		public String getId () {
			return id;
		}
		
		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getModuleDir() {
			return moduleDir;
		}

		public ModuleConfig setModuleDir(String moduleDir) {
			this.moduleDir = moduleDir;
			return this;
		}

		public String getModuleDataDir() {
			return moduleDataDir;
		}

		public ModuleConfig setModuleDataDir(String moduleDataDir) {
			this.moduleDataDir = moduleDataDir;
			
			return this;
		}
		
		

		@Override
		public int hashCode() {
			int hash = 3;
			hash = 37 * hash + Objects.hashCode(this.id);
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final ModuleConfig other = (ModuleConfig) obj;
			if (!Objects.equals(this.id, other.id)) {
				return false;
			}
			return true;
		}
		
		
	}
}
