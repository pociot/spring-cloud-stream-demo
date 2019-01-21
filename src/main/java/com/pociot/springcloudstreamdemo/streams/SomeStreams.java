package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(SomeBindings.class)
public class SomeStreams {

  private static final Logger log = LoggerFactory.getLogger(SomeStreams.class);


  @StreamListener
  public void process(@Input(SomeBindings.INPUT) KStream<String, String> stream) {
    stream.foreach((key, value) -> log.info("process() - value: {}", value));
  }

  @StreamListener
  public void processSecond(@Input(SomeBindings.SECOND_INPUT) KStream<String, String> stream) {
    stream.foreach((key, value) -> log.info("processSecond() - value: {}", value));
  }
}
