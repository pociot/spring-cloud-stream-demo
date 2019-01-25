package com.pociot.springcloudstreamdemo;

import com.pociot.springcloudstreamdemo.streams.SomeStreams;
import java.util.Properties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SomeStreamsTest {

  private final Logger log = LoggerFactory.getLogger(SomeStreamsTest.class);

  private ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());
  private TopologyTestDriver topologyTestDriver;

  @Before
  public void testStream() {
    SomeStreams someStreams = new SomeStreams(null);

    StreamsBuilder builder = new StreamsBuilder();
    KStream<String,String> stream = builder.stream("input-topic");
    someStreams.process(stream).to("output-topic");
    Topology topology = builder.build();

    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.StringSerde.class);
    this.topologyTestDriver = new TopologyTestDriver(topology, props);
  }

  @After
  public void tearDown() {
    this.topologyTestDriver.close();
  }

  @Test
  public void doTest() {
    topologyTestDriver.pipeInput(factory.create("input-topic","key", "some-message"));
    ProducerRecord<String, String> outputRecord = topologyTestDriver.readOutput("output-topic", new StringDeserializer(), new StringDeserializer());
    log.info("Output value: {}", outputRecord.value());
    OutputVerifier.compareValue(outputRecord, "SOME-MESSAGE");

  }


}
