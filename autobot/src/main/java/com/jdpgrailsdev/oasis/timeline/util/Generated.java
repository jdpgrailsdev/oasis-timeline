package com.jdpgrailsdev.oasis.timeline.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Custom annotation used to suppress code coverage of methods.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Generated {

}
