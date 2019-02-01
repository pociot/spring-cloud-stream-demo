package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(SomeBindings.class)
public class SomeStreams {

  private static final Logger log = LoggerFactory.getLogger(SomeStreams.class);

  private static final String ROUTER_CHANNEL = "routerChannel";

  @Bean(name = ROUTER_CHANNEL)
  public MessageChannel routerChannel() {
    return new DirectChannel();
  }

  @StreamListener(SomeBindings.INPUT)
  public void processFirst(KStream<String, String> stream) {
    stream.foreach((key, value) -> {
      log.info("Process first - value: {}", value);
      routerChannel().send(MessageBuilder.withPayload(value).build());
    });
  }

  @StreamListener(SomeBindings.SECOND_INPUT)
  @SendTo(SomeBindings.OTHER_OUTPUT)
  public KStream<String, String> process(KStream<String, String> stream) {
    return stream.mapValues((value) -> {
      log.info("Process - value: {}", value);
      return value.toUpperCase();
    });
  }
}
