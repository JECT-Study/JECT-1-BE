package ject.mycode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients(basePackages = "ject.mycode.domain.auth")
@ImportAutoConfiguration(FeignAutoConfiguration.class)
@SpringBootApplication
@EnableJpaAuditing
public class MycodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycodeApplication.class, args);
	}

}
