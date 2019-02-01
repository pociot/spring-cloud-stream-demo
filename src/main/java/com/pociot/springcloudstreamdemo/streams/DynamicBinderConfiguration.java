package com.pociot.springcloudstreamdemo.streams;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.MethodInvokingRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class DynamicBinderConfiguration {

  private static final Logger log = LoggerFactory.getLogger(DynamicBinderConfiguration.class);

  private static final String ROUTER_CHANNEL = "routerChannel";
  private final DestinationResolver<MessageChannel> resolver;

  public DynamicBinderConfiguration(
      @Qualifier("binderAwareChannelResolver") DestinationResolver<MessageChannel> resolver) {
    this.resolver = resolver;
  }

  @Bean
  @ServiceActivator(inputChannel = ROUTER_CHANNEL)
  public MethodInvokingRouter router() throws NoSuchMethodException {
    MultiChannelNameRoutingBean routingBean = new MultiChannelNameRoutingBean();
    Method routingMethod = routingBean.getClass().getMethod("routePayload", String.class);
    MethodInvokingRouter router = new MethodInvokingRouter(routingBean, routingMethod);
    router.setChannelResolver(resolver);
    return router;
  }


  public static class SingleChannelNameRoutingBean {

    public String routePayload(String name) {
      log.info("routePayload() - name: {}", name);
      return name + "-channel";
    }

    public String routeByHeader(@Header("targetChannel") String name) {
      return name + "-channel";
    }

    public String routeMessage(Message<?> message) {
      if (message.getPayload().equals("foo")) {
        return "foo-channel";
      }
      else if (message.getPayload().equals("bar")) {
        return "bar-channel";
      }
      return null;
    }
  }

  public static class MultiChannelNameRoutingBean {

    public List<String> routePayload(String name) {
      List<String> results = new ArrayList<String>();
      if (name.equals("first-topic")) {
        results.add("first-topic-channel");
        results.add("second-topic-channel");
      } else {
        results.add("second-topic-channel");
      }
      return results;
    }

    public List<String> routeMessage(Message<?> message) {
      List<String> results = new ArrayList<String>();
      if (message.getPayload().equals("foo") || message.getPayload().equals("bar")) {
        results.add("foo-channel");
        results.add("bar-channel");
      }
      return results;
    }

    public String[] routeMessageToArray(Message<?> message) {
      String[] results = null;
      if (message.getPayload().equals("foo") || message.getPayload().equals("bar")) {
        results = new String[2];
        results[0] = "foo-channel";
        results[1] = "bar-channel";
      }
      return results;
    }
  }
}
