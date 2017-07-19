package com.sharkbaitextraordinaire.bootnotifier;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableAutoConfiguration
@EnableScheduling
public class SpringbootDemoApplication {
	
    private static final Logger log = LoggerFactory.getLogger(SpringbootDemoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDemoApplication.class, args);

	}
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NamedParameterJdbcTemplate namedParamTemplate;
}
