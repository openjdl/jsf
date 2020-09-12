package com.openjdl.jsf.webflux.controller.editor;

import com.openjdl.jsf.core.utils.DateUtils;
import org.springframework.beans.propertyeditors.PropertiesEditor;

import java.util.Optional;

/**
 * Created at 2020-08-05 15:14:39
 *
 * @author kidal
 * @since 0.1.0
 */
public class DatePropertyEditor extends PropertiesEditor {
  /**
   *
   */
  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    setValue(DateUtils.uncertainToDateSafely(text));
  }

  /**
   *
   */
  @Override
  public String getAsText() {
    return Optional.ofNullable(DateUtils.toStringSafely(getValue())).orElse("");
  }
}

