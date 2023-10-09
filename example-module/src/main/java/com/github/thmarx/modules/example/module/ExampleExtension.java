/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.github.thmarx.modules.example.module;

/*-
 * #%L
 * example-module
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

import com.github.thmarx.modules.api.annotation.Extension;
import com.github.thmarx.modules.example.api.GetStringExtensionPoint;

/**
 *
 * @author t.marx
 */
@Extension(GetStringExtensionPoint.class)
public class ExampleExtension extends GetStringExtensionPoint {

	@Override
	public String getString() {
		return "a string";
	}

	@Override
	public void init() {
		
	}

}
