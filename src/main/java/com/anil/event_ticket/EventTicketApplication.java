package com.anil.event_ticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EventTicketApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventTicketApplication.class, args);
	}

}
