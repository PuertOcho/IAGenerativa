package com.nuka.nuka_server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfiguration {

	@Bean
	MongoMappingContext springDataMongoMappingContext() {
	    return new MongoMappingContext();
	}
	
}
