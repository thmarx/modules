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


/**
 *
 * @author marx
 * @param <C>
 */
public abstract class BaseExtension<C extends Context> implements ExtensionPoint<C> {

	protected ModuleConfiguration configuration;
	
	private C context;

	@Override
	public void setContext(C context) {
		this.context = context;
	}
	
	public C getContext () {
		return this.context;
	}
	
	@Override
	public void setConfiguration(ModuleConfiguration configuration) {
		this.configuration = configuration;
	}
	
	
}
