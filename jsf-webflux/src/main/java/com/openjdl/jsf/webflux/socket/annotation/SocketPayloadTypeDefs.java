package com.openjdl.jsf.webflux.socket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-12-29 21:04:31
 *
 * @author kidal
 * @since 2.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SocketPayloadTypeDefs {
  SocketPayloadTypeDef[] value();
}
