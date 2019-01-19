package com.pociot.springcloudstreamdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.properties.KafkaProducerProperties;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver.NewDestinationBindingCallback;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringCloudStreamDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamDemoApplication.class, args);
	}

	@Bean
	public NewDestinationBindingCallback<KafkaProducerProperties> dynamicConfigurer() {
		return (name, channel, props, extended) -> {
			System.out.println("inside configurer name: " + name + " channel: " + channel + " props: " + extended);
		};
	}
}

