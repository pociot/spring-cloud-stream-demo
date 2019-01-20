package com.pociot.springcloudstreamdemo.streams;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.MethodInvokingRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(SomeBindings.class)
public class SomeStreams {

  private static final Logger log = LoggerFactory.getLogger(SomeStreams.class);

  private static final String ROUTER_CHANNEL = "routerChannel";
  private final BinderAwareChannelResolver resolver;

  public SomeStreams(BinderAwareChannelResolver resolver) {
    this.resolver = resolver;
  }

  @Bean(name = ROUTER_CHANNEL)
  public MessageChannel routerChannel() {
    return new DirectChannel();
  }

  @StreamListener
  public void process(@Input(SomeBindings.INPUT) KStream<String, String> stream) {
    stream.foreach((key, value) -> {
      log.info("Process - value: {}", value);
      routerChannel().send(MessageBuilder.withPayload(value).build());
    });
  }

  @Bean
  @ServiceActivator(inputChannel = ROUTER_CHANNEL)
  public MethodInvokingRouter router() throws NoSuchMethodException {
    SingleChannelNameRoutingBean testBean = new SingleChannelNameRoutingBean();
    Method routingMethod = testBean.getClass().getMethod("routePayload", String.class);
    MethodInvokingRouter router = new MethodInvokingRouter(testBean, routingMethod);
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
      if (name.equals("foo") || name.equals("bar")) {
        results.add("foo-channel");
        results.add("bar-channel");
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
