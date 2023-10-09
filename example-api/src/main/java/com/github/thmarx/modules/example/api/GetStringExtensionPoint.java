/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.github.thmarx.modules.example.api;

import com.github.thmarx.modules.api.Context;
import com.github.thmarx.modules.api.ExtensionPoint;
import com.github.thmarx.modules.api.ModuleConfiguration;

/*-
 * #%L
 * example-api
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
public abstract class GetStringExtensionPoint implements ExtensionPoint<TestContext> {

	protected TestContext context;
	protected ModuleConfiguration configuration;
	
	@Override
	public void setConfiguration(ModuleConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void setContext(TestContext context) {
		this.context = context;
	}
	
	
	
	public abstract String getString();
}
