package com.mintic.lastChallenge;

import com.mintic.lastChallenge.controller.ProductController;
import com.mintic.lastChallenge.model.ProductRepository;
import com.mintic.lastChallenge.view.StoreGUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages={
		"com.mintic.lastChallenge", "com.mintic.lastChallenge.model"})
public class LastChallengeApplication {
	@Autowired
	ProductRepository productRepository;
	public static void main(String[] args) {
		//SpringApplication.run(LastChallengeApplication.class, args);
		SpringApplicationBuilder builder = new SpringApplicationBuilder(LastChallengeApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);
	}

	@Bean
	ApplicationRunner applicationRunner (){
		return args -> {
			StoreGUI view = new StoreGUI();
			ProductController controller = new ProductController(productRepository, view);
		};
	}
}
