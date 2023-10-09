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



import com.github.thmarx.modules.api.DefaultServiceRegistry;
import com.github.thmarx.modules.api.services.SimpleImple;
import com.github.thmarx.modules.api.services.SimpleImple2;
import com.github.thmarx.modules.api.services.SimpleService;
import org.assertj.core.api.Assertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class ServicesTest {
	
	private SimpleService s1 = new SimpleImple();
	private SimpleService s2 = new SimpleImple2();
	
	private DefaultServiceRegistry services;
	
	public ServicesTest() {
	}
	
	@BeforeClass
	public void setup () {
		
		services = new DefaultServiceRegistry();
		
		services.register(SimpleService.class, s1);
		services.register(SimpleService.class, s2);
	}
	
	@Test
	public void test_exists () {
		Assertions.assertThat(services.exists(SimpleService.class)).isTrue();
	}
	
	@Test
	public void test_get () {
		Assertions.assertThat(services.get(SimpleService.class)).hasSize(2);
	}
	
	@Test
	public void test_single () {
		Assertions.assertThat(services.single(SimpleService.class).get()).isEqualTo(s1);
	}
	
	@Test(dependsOnMethods = {"test_exists", "test_get", "test_single"})
	public void test_unregister_first () {
		services.unregister(SimpleService.class, s1);
		Assertions.assertThat(services.get(SimpleService.class)).hasSize(1);
		Assertions.assertThat(services.single(SimpleService.class).get()).isEqualTo(s2);
	}
	
	@Test(dependsOnMethods = {"test_unregister_first"})
	public void test_unregister_second () {
		services.unregister(SimpleService.class, s2);
		Assertions.assertThat(services.exists(SimpleService.class)).isFalse();
	}
}
