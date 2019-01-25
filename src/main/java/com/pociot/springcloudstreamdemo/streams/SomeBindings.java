package com.pociot.springcloudstreamdemo.streams;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SomeBindings {

  String INPUT = "input";
  String SECOND_INPUT = "secondInput";
  String OUTPUT = "output";
  String OTHER_OUTPUT = "otherOutput";

  @Input(INPUT)
  KStream<String, String> input();

  @Input(SECOND_INPUT)
  KStream<String, String> secondInput();

  @Output(OUTPUT)
  MessageChannel output();

  @Output(OTHER_OUTPUT)
  KStream<String, String> otherOutput();
}
