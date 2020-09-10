package io.tdi.jsf.core;

import io.tdi.jsf.core.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
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
    JsfService[] services = JsfMicroService.SERVICES.stream()
      .sorted(Comparator.comparingInt(c -> {
        if (c instanceof Ordered) {
          return ((Ordered) c).getOrder();
        } else if (c.getClass().isAnnotationPresent(Order.class)) {
          return c.getClass().getAnnotation(Order.class).value();
        } else {
          return 0;
        }
      }))
      .toArray(JsfService[]::new);

    // dispatch event
    if (applicationEvent instanceof ContextRefreshedEvent) {
      if (JsfMicroService.launched) {
        return;
      }

      // 设置元数据
      Environment environment = ((ContextRefreshedEvent) applicationEvent).getApplicationContext().getEnvironment();
      Integer serverPort = environment.getProperty("server.port", Integer.class);
      String springApplicationName = environment.getProperty("spring.application.name", String.class);

      JsfMicroService.metadata.setName(springApplicationName);
      JsfMicroService.metadata.getInstance().setPort(serverPort);
      JsfMicroService.metadata.getInstance().setUuid(
        String.format("%s:%d",
          JsfMicroService.metadata.getInstance().getLanIp(),
          JsfMicroService.metadata.getInstance().getPort()
        ));
      JsfMicroService.LOG.info("Launching\n{}", JsonUtils.toPrettyString(JsfMicroService.metadata));

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
  private void dispatchContextRefreshedEvent(JsfService[] services, ContextRefreshedEvent event) {
    // services
    for (JsfService service : services) {
      try {
        long startTime = System.nanoTime();
        service.initializeJsfService();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logInitializing(service.getJsfServiceName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Initialize {} failed", service.getJsfServiceName(), e);

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
  private void dispatchApplicationReadyEvent(JsfService[] services, ApplicationReadyEvent event) {
    for (JsfService service : services) {
      // do
      try {
        long startTime = System.nanoTime();
        service.startJsfService();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logRunning(service.getJsfServiceName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Start {} failed", service.getJsfServiceName(), e);

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
  private void dispatchContextClosedEvent(JsfService[] services, ContextClosedEvent event) {
    for (JsfService service : services) {
      // do
      try {
        long startTime = System.nanoTime();
        service.closeJsfService();
        long elapsedTime = System.nanoTime() - startTime;
        JsfMicroService.logClosed(service.getJsfServiceName(), elapsedTime);
      } catch (Exception e) {
        // log
        JsfMicroService.LOG.error(" -> Close {} failed", service.getJsfServiceName(), e);
      }
    }

    // listeners
    JsfMicroService.LISTENERS.forEach(JsfMicroServiceListener::onMicroServiceClosed);
  }
}
