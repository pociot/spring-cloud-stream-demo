package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SomeBindings {

  final String INPUT = "input";
  final String OUTPUT = "output";

  @Input(INPUT)
  KStream<String, String> input();

  @Output(OUTPUT)
  MessageChannel output();
}
