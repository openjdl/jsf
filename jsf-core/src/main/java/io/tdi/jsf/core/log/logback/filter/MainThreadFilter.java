package io.tdi.jsf.core.log.logback.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;


/**
 * Created at 2020-08-04 17:30:00
 *
 * @author kidal
 * @since 0.1.0
 */
public class MainThreadFilter extends Filter<ILoggingEvent> {
  /**
   * 主线程名
   */
  public static final String MAIN_THREAD_NAME = "main";

  /**
   *
   */
  @Override
  public FilterReply decide(ILoggingEvent event) {
    if (MAIN_THREAD_NAME.equalsIgnoreCase(event.getThreadName())) {
      return FilterReply.ACCEPT;
    } else {
      return FilterReply.DENY;
    }
  }
}
