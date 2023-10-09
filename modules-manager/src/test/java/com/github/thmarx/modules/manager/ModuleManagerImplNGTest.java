/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/EmptyTestNGTest.java to edit this template
 */
package com.github.thmarx.modules.manager;

/*-
 * #%L
 * modules-manager
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

import com.github.thmarx.modules.api.Context;
import com.github.thmarx.modules.api.ModuleManager;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author t.marx
 */
public class ModuleManagerImplNGTest {
	
	

	@Test
	void testSomeMethod() throws IOException {
		final Path modulesPath = Path.of("target/modules");
		Files.createDirectory(modulesPath);
		final Path modulesDataPath = Path.of("target/modules_data");
		Files.createDirectory(modulesDataPath);
		ModuleManager moduleManager = ModuleManagerImpl.create(modulesPath.toFile(), modulesDataPath.toFile(), new Context() {
		});
		
		moduleManager.getModuleIds();
	}
	
}
