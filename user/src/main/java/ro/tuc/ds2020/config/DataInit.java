package ro.tuc.ds2020.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.services.UserService;

@Component
@AllArgsConstructor
public class DataInit implements CommandLineRunner {

    private final UserService userService;

    @Override
    public void run(String... args) throws Exception {
        UserDTO admin = new UserDTO("admin", "1234", "admin");
        UserDTO client1 = new UserDTO("client1", "1234", "client");
        UserDTO client2 = new UserDTO("client2", "1234", "client");

        userService.insertUser(admin);
        userService.insertUser(client1);
        userService.insertUser(client2);

        System.out.println("Mock User data has been seeded into the database.");
    }
}
