package de.marx_software.cms.headless.modules.api;



import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Test;

/**
 *
 * @author marx
 */
public class ComplexServiceTest {

    DefaultServiceRegistry registry;

    @Test
    public void doTest() {
        registry = new DefaultServiceRegistry();

        registry.register(Service.class, new ServiceImpl());

        Service service = registry.single(Service.class).get();
        final BeanImpl beanImpl = new BeanImpl();
        beanImpl.add("name", "Hans");
        service.register("b1", beanImpl);

        service = registry.single(Service.class).get();
        final Map<String, Bean> beans = service.beans();
        System.out.println(beans);
        System.out.println(beans.get("b1"));
    }

    public interface Service {

        public Map<String, Bean> beans();

        public void register(final String name, final Bean bean);

        public void unregister(final String name);
    }

    public static class ServiceImpl implements Service {

        protected Map<String, Bean> beans;

        public ServiceImpl() {
            beans = new HashMap<>();
        }

        public Map<String, Bean> beans() {
            return beans;
        }

        public void register(final String name, final Bean bean) {
            this.beans.put(name, bean);
        }

        public void unregister(final String name) {
            this.beans.remove(name);
        }
    }

    public interface Bean {

        public void add(final String name, final String value);

        public void remove(final String name);
    }

    public static class BeanImpl implements Bean {

        protected Map<String, String> items;

        public BeanImpl() {
            items = new HashMap<>();
        }

        public void add(final String name, final String value) {
            items.put(name, value);
        }

        public void remove(final String name) {
            items.remove(name);
        }
    }

    public static class Keys {

        private static final String A = "a";
        private static final String B = "b";
    }
}
