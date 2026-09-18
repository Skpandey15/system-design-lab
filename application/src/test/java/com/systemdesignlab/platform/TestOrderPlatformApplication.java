package com.systemdesignlab.platform;

import org.springframework.boot.SpringApplication;

public class TestOrderPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.from(OrderPlatformApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
