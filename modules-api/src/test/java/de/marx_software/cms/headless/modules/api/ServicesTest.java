package de.marx_software.cms.headless.modules.api;



import de.marx_software.cms.headless.modules.api.services.SimpleImple;
import de.marx_software.cms.headless.modules.api.services.SimpleImple2;
import de.marx_software.cms.headless.modules.api.services.SimpleService;
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
