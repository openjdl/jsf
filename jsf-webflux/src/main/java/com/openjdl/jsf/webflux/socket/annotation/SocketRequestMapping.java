package com.openjdl.jsf.webflux.socket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-12-23 15:50:25
 *
 * @author zink
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SocketRequestMapping {
  long value() default 0;
}
