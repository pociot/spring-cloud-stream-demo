package com.pociot.springcloudstreamdemo.streams;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.binder.kafka.properties.KafkaProducerProperties;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver.NewDestinationBindingCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicBinderCallback {

  private final Logger log = LoggerFactory.getLogger(DynamicBinderCallback.class);

  @Bean
  public NewDestinationBindingCallback<KafkaProducerProperties> dynamicConfigurer() {
    return (name, channel, props, extended) -> {
      Map<String, String> extendedProps = new HashMap<>();
      extendedProps.put("key.serializer", StringSerializer.class.getName());
      extendedProps.put("value.serializer", StringSerializer.class.getName());
      extendedProps.put("client.id", name + UUID.randomUUID().toString());
      extended.setConfiguration(extendedProps);
      log.info("Dynamic configurer - name:{}, channel:{}, props:{}", name, channel, props);
    };
  }
}
