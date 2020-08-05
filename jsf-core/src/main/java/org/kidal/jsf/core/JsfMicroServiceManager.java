package org.kidal.jsf.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/**
 * Created at 2020-08-04 23:11:55
 *
 * @author kidal
 * @since 0.1.0
 */
@Component
public class JsfMicroServiceManager implements ApplicationListener<ApplicationEvent> {
  /**
   *
   */
  @Override
  public void onApplicationEvent(@NotNull ApplicationEvent applicationEvent) {
    // get sorted services
    JsfMicroServiceModule[] services = JsfMicroService.SERVICES.stream()
      .sorted(Comparator.comparingInt(c -> {
        if (c instanceof Ordered) {
          return ((Ordered) c).getOrder();
        } else if (c.getClass().isAnnotationPresent(Order.class)) {
          return c.getClass().getAnnotation(Order.class).value();
        } else {
          return 0;
        }
      }))
      .toArray(JsfMicroServiceModule[]::new);

    // dispatch event
    if (applicationEvent instanceof ContextRefreshedEvent) {
      if (JsfMicroService.launched) {
        return;
      }

      // record
      JsfMicroService.launched = true;

      // log
      JsfMicroService.LOG.info("Initializing   {}:{}", JsfMicroService.metadata.getGroup(), JsfMicroService.metadata.getName());

      // dispatch
      dispatchContextRefreshedEvent(services, (ContextRefreshedEvent) applicationEvent);
    } else if (applicationEvent instanceof ApplicationReadyEvent) {
      // dispatch
      dispatchApplicationReadyEvent(services, (ApplicationReadyEvent) applicationEvent);

      // record
      JsfMicroService.running = true;

      // log
      JsfMicroService.LOG.info("Initialized    {}:{}", JsfMicroService.metadata.getGroup(), JsfMicroService.metadata.getName());
    } else if (applicationEvent instanceof ContextClosedEvent) {
      // dispatch
      dispatchContextClosedEvent(services, (ContextClosedEvent) applicationEvent);

      // log
      JsfMicroService.LOG.info("Closed         {}:{}", JsfMicroService.metadata.getGroup(), JsfMicroService.metadata.getName());
    }
  }

  /**
   *
   */
  private void dispatchContextRefreshedEvent(JsfMicroServiceModule[] services, ContextRefreshedEvent event) {
    // services
    for (JsfMicroServiceModule service : services) {
      try {
        long startTime = System.nanoTime();
        service.initializeJsfMicroServiceModule();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logInitializing(service.getJsfMicroServiceModuleName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Initialize {} failed", service.getJsfMicroServiceModuleName(), e);

        // close application
        ((ConfigurableApplicationContext) event.getApplicationContext()).close();
      }
    }

    // listeners
    JsfMicroService.LISTENERS.forEach(JsfMicroServiceListener::onMicroServiceInitialized);
  }

  /**
   *
   */
  private void dispatchApplicationReadyEvent(JsfMicroServiceModule[] services, ApplicationReadyEvent event) {
    for (JsfMicroServiceModule service : services) {
      // do
      try {
        long startTime = System.nanoTime();
        service.startJsfMicroServiceModule();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logRunning(service.getJsfMicroServiceModuleName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Start {} failed", service.getJsfMicroServiceModuleName(), e);

        // close application
        event.getApplicationContext().close();
      }
    }

    // listeners
    JsfMicroService.LISTENERS.forEach(JsfMicroServiceListener::onMicroServiceStarted);
  }

  /**
   *
   */
  private void dispatchContextClosedEvent(JsfMicroServiceModule[] services, ContextClosedEvent event) {
    for (JsfMicroServiceModule service : services) {
      // do
      try {
        long startTime = System.nanoTime();
        service.closeJsfMicroServiceModule();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logClosed(service.getJsfMicroServiceModuleName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Close {} failed", service.getJsfMicroServiceModuleName(), e);
      }
    }

    // listeners
    JsfMicroService.LISTENERS.forEach(JsfMicroServiceListener::onMicroServiceClosed);
  }
}
