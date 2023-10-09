package de.marx_software.cms.headless.modules.api.annotation;


import de.marx_software.cms.headless.modules.api.ExtensionPoint;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import java.lang.annotation.Target;

@Documented
@Retention(SOURCE)
@Target(TYPE)
public @interface Extension {
	Class<? extends ExtensionPoint> value();
}
