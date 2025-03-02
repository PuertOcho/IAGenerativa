package com.nuka.nuka_server;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.nuka.nuka_server.repository.NukaMsjRepository;
import com.nuka.nuka_server.repository.ShoppingListRepository;
import com.nuka.nuka_server.repository.UserRepository;

@SpringBootApplication
@EnableConfigurationProperties
@EnableMongoRepositories
@EnableScheduling
@ComponentScan(basePackages = "com.nuka.nuka_server")
public class NukaServerApplication {

	@Autowired(required = false)
	public UserRepository userRepository;

	@Autowired(required = false)
	public ShoppingListRepository shoppingListRepository;

	@Autowired(required = false)
	public NukaMsjRepository nukaMsjRepository;
	    
	public static void main(String[] args) throws IOException {
	    System.setProperty("server.port", "9898");
	    System.setProperty("java.awt.headless", "false");
	    SpringApplication.run(NukaServerApplication.class, args);
	}
}
