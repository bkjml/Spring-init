package se.mathias.microservices.composite.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

/**
 * The type Product composite service application.
 */
@SpringBootApplication
@ComponentScan("se.mathias")
public class ProductCompositeServiceApplication {

	/**
	 * Rest template rest template.
	 *
	 * @return the rest template
	 */
	@Bean
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProductCompositeServiceApplication.class, args);
	}

}
