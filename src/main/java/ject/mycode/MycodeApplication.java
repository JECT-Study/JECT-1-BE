package ject.mycode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MycodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycodeApplication.class, args);
	}

}
