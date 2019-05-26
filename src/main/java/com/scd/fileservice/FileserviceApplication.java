package com.scd.fileservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.scd.filesdk","com.scd.fileservice"})
public class FileserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileserviceApplication.class, args);
	}

}
