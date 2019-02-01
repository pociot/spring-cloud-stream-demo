package com.pociot.springcloudstreamdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringCloudStreamDemoApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringCloudStreamDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudStreamDemoApplication.class, args);
	}
}

