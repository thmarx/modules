/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.github.thmarx.modules.example.app;

import com.github.thmarx.modules.api.ModuleManager;
import com.github.thmarx.modules.example.api.GetStringExtensionPoint;
import com.github.thmarx.modules.example.api.TestContext;
import com.github.thmarx.modules.manager.ModuleAPIClassLoader;
import com.github.thmarx.modules.manager.ModuleManagerImpl;
import java.io.File;
import java.util.List;

/*-
 * #%L
 * example-app
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

/**
 *
 * @author t.marx
 */
public class ExampleApp {

    public static void main(String[] args) throws Exception {
		var classLoader = new ModuleAPIClassLoader(ClassLoader.getSystemClassLoader(), 
				List.of("org.slf4j", "com.github.thmarx.modules")
		);
        ModuleManager manager = ModuleManagerImpl.create(new File("modules"), new File("modules_data"), new TestContext(), classLoader);
		
		System.out.println(manager.getModuleIds());
		System.out.println(manager.module("example-module"));
		manager.activateModule("example-module");
		List<GetStringExtensionPoint> extensions = manager.extensions(GetStringExtensionPoint.class);
		System.out.println(extensions.getFirst().getString());
    }
}
