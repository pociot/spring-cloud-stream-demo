package com.pociot.springcloudstreamdemo.streams;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@EnableBinding(SomeBindings.class)
public class EventSource implements ApplicationRunner {

  private final MessageChannel out;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  public EventSource(SomeBindings myBindings) {
    this.out = myBindings.output();
  }

  @Override
  public void run(ApplicationArguments args) {
    Runnable runnable = () -> out
        .send(MessageBuilder
            .withPayload(ThreadLocalRandom.current().nextInt(2) == 0 ? "first-topic" : "second-topic")
            .build()
        );
    Executors.newSingleThreadScheduledExecutor()
        .scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
  }
}
