package com.openjdl.jsf.webflux.modbus.dtu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created at 2020-12-08 10:03:50
 *
 * @author kidal
 * @since 0.5
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ModbusDtuResponseMapping {
}
