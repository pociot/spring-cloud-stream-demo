package com.pociot.springcloudstreamdemo;

import com.pociot.springcloudstreamdemo.streams.EventSource;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude = {
    TestSupportBinderAutoConfiguration.class
})
@ComponentScan(
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        value = {
            EventSource.class
        }
    )
)
public class SomeStreamsIntegrationTest {

  @ClassRule
  public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true,
      "some-topic",
      "first-topic-channel",
      "second-topic-channel"
  );

  private static EmbeddedKafkaBroker kafkaBroker = embeddedKafka.getEmbeddedKafka();

  private static Consumer<String, String> firstTopicConsumer;

  @BeforeClass
  public static void someTest() {
    System
        .setProperty("spring.cloud.stream.kafka.binder.brokers", kafkaBroker.getBrokersAsString());
    System.setProperty("spring.cloud.stream.kafka.streams.binder.brokers",
        kafkaBroker.getBrokersAsString());
    System.setProperty("spring.cloud.stream.kafka.streams.binder.zkNodes",
        kafkaBroker.getZookeeperConnectionString());
    Map<String, Object> consumerProps = KafkaTestUtils
        .consumerProps("test-group", "true", kafkaBroker);
    consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(
        consumerProps);
    firstTopicConsumer = cf.createConsumer();
    kafkaBroker.consumeFromEmbeddedTopics(firstTopicConsumer, "first-topic-channel", "second-topic-channel");
  }

  @AfterClass
  public static void cleanUp() {
    firstTopicConsumer.close();
  }

  @Test
  public void sendAndReceive() throws Exception {
    Map<String, Object> producerProps = KafkaTestUtils.producerProps(kafkaBroker);
    producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    DefaultKafkaProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(
        producerProps);
    try {
      KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory, true);
      kafkaTemplate.send("some-topic", "key", "first-topic");
      ConsumerRecord<String, String> firstTopicRecord = KafkaTestUtils
          .getSingleRecord(firstTopicConsumer, "first-topic-channel");
      ConsumerRecord<String, String> secondTopicRecord = KafkaTestUtils
          .getSingleRecord(firstTopicConsumer, "second-topic-channel");
      Assert.assertEquals("Messages should be equal", firstTopicRecord.value(), "first-topic");
      Assert.assertEquals("Messages should be equal", secondTopicRecord.value(), "first-topic");
    } finally {
      producerFactory.destroy();
    }
  }
}
