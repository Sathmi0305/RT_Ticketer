package com.vendor.vendorticketsystem;

import com.vendor.vendorticketsystem.worker.Customer;
import com.vendor.vendorticketsystem.worker.Vendor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.ExecutorService;

@SpringBootApplication
public class VendorticketsystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(VendorticketsystemApplication.class, args);
	}
}

