package contentcalendar.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
		log.info("^_^ Application is running.");
	}

//	@Bean
//	CommandLineRunner run(UserService userService) {
//		return args -> {
//			userService.saveRole(new Role(null, "ROLE_USER"));
//			userService.saveRole(new Role(null, "ROLE_MANAGER"));
//			userService.saveRole(new Role(null, "ROLE_ADMIN"));
//
//			userService.saveUser(new User(null, "John Travolta", "john", "1234", new ArrayList<>()));
//			userService.saveUser(new User(null, "Will Smith", "will", "1234", new ArrayList<>()));
//			userService.saveUser(new User(null, "Jim Carry", "jim", "1234", new ArrayList<>()));
//
//		};
//	}

}
