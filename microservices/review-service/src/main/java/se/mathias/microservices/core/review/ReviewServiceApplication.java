package se.mathias.microservices.core.review;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * The type Review service application.
 */
@SpringBootApplication
@ComponentScan("se.mathias")
public class ReviewServiceApplication {

	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(ReviewServiceApplication.class, args);
	}

}
