package com.pociot.springcloudstreamdemo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.HeaderMode;
import org.springframework.cloud.stream.binder.kafka.properties.KafkaProducerProperties;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver.NewDestinationBindingCallback;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCloudStreamDemoApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringCloudStreamDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamDemoApplication.class, args);
	}

	@Bean
	public NewDestinationBindingCallback<KafkaProducerProperties> dynamicConfigurer() {
		return (name, channel, props, extended) -> {
			props.setHeaderMode(HeaderMode.raw);
			Map<String, String> extendedProps = new HashMap<>();
			extendedProps.put("key.serializer", StringSerializer.class.getName());
			extendedProps.put("value.serializer", StringSerializer.class.getName());
			extended.setConfiguration(extendedProps);
			log.info("Dynamic configurer - name:{}, channel:{}, props:{}", name, channel, props);
			extended.setConfiguration(Collections.singletonMap("client.id", name + UUID.randomUUID().toString()));
		};
	}
}

