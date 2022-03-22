package com.rafabene.mancalagame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MancalaGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(MancalaGameApplication.class, args);
	}

}
