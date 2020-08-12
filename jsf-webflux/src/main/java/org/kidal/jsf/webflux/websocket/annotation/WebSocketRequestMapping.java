package org.kidal.jsf.webflux.websocket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-08-12 22:04:54
 *
 * @author kidal
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface WebSocketRequestMapping {
  String value() default "";
}
