package org.kidal.jsf.core.converter;

import org.jetbrains.annotations.NotNull;
import org.kidal.jsf.core.utils.DateUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

/**
 * Created at 2020-08-04 17:59:05
 *
 * @author kidal
 * @since 0.1.0
 */
public class StringToDateConverter implements Converter<String, Date> {
  @Override
  public Date convert(@NotNull String source) {
    return DateUtils.uncertainToDateSafely(source);
  }
}
