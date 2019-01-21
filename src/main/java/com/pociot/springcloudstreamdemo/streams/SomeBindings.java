package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SomeBindings {

  String INPUT = "input";
  String OUTPUT = "output";
  String SECOND_INPUT = "secondInput";

  @Input(INPUT)
  KStream<String, String> input();

  @Output(OUTPUT)
  MessageChannel output();

  @Input(SECOND_INPUT)
  KStream<String, String> secondInput();
}
