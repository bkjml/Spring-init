package se.mathias.microservices.core.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The type Product service application.
 */
@SpringBootApplication
@ComponentScan("se.mathias")
public class ProductServiceApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

}
