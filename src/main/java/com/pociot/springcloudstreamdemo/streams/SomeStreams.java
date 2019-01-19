package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.router.ExpressionEvaluatingRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(SomeBindings.class)
public class SomeStreams {

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
      System.out.println(value);
      routerChannel().send(MessageBuilder.withPayload(value).build());
    });
  }

  @Bean
  @ServiceActivator(inputChannel = ROUTER_CHANNEL)
  public ExpressionEvaluatingRouter router() {
    ExpressionEvaluatingRouter router =
        new ExpressionEvaluatingRouter(new SpelExpressionParser().parseExpression("payload"));
    router.setChannelResolver(resolver);
    return router;
  }
}
