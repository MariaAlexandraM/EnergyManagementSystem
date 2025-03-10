package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.LoginDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    // sincronizare cu device: adaug sau sterg user

    // private String DEVICE_SERVICE_URL = "http://localhost:8081";
    private String DEVICE_SERVICE_URL = "http://device.localhost";
    // private String DEVICE_SERVICE_URL = "http://device_app:8081";

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public List<UserDTO> findAll() {
        List<User> userList = userRepository.findAll();
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO findUserById(int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDTO(user.get());
    }

    public int insertUser(UserDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);

        try {
            // trimit ca imi creez user
            restTemplate.postForObject(DEVICE_SERVICE_URL + "/user_in_device", new UserDTO(user.getId()), Void.class);
        } catch (Exception e) {
            LOGGER.error("Failed to sync user creation with Device Microservice: " + e.getMessage());
        }

        LOGGER.debug("User with id {} was inserted in db", user.getId());
        System.out.println("User with id " + user.getId() + " was inserted in db");
        return user.getId();
    }

    public int updateUser(int id, UserDTO userDTO) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (!user.getName().equals(userDTO.getName())) {
                user.setName(userDTO.getName());
            }

            if (!user.getPassword().equals(userDTO.getPassword())) {
                user.setPassword(userDTO.getPassword());
            }

            if (!user.getRole().equals(userDTO.getRole())) {
                user.setRole(userDTO.getRole());
            }

            User updatedUser = userRepository.save(user);
            return updatedUser.getId();

        } else {
            LOGGER.error("User with id {} was not found in db", userDTO.getId());
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + userDTO.getId());
        }
    }

    public int deleteUser(int id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }

        try {
            // trimit sa il sterg din Device
            restTemplate.delete(DEVICE_SERVICE_URL + "/user_in_device/" + id);
            LOGGER.info("Successfully synced user deletion with DeviceService");
        } catch (Exception e) {
            LOGGER.error("Failed to sync user deletion with DeviceService: " + e.getMessage());
        }

        userRepository.deleteById(id);
        return id;
    }

    public UserDTO login(UserDTO userDTO) {
        Optional<User> userOptional = Optional.ofNullable(userRepository.findByNameAndPassword(userDTO.getName(), userDTO.getPassword()));

        if (!userOptional.isPresent()) {
            LOGGER.error("User with name {} was not found or password is incorrect", userDTO.getName());
            throw new ResourceNotFoundException("Invalid username or password");
        }

        User user = userOptional.get();
        return UserBuilder.toUserDTO(user);
    }


}
