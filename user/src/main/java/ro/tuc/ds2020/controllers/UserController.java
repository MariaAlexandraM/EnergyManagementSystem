package ro.tuc.ds2020.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.services.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> dtos = userService.findAll();
        for(UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserController.class).getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") int id) {
        UserDTO dto = userService.findUserById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Autowired
    private RestTemplate restTemplate;

    @PostMapping()
    public ResponseEntity<Integer> insertUser(@Valid @RequestBody UserDTO userDTO) {
        int userId = userService.insertUser(userDTO);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }


//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Integer> deleteUser(@PathVariable("id") int user_id) {
//        int id = userService.deleteUser(user_id);
//        return new ResponseEntity<>(id, HttpStatus.OK);
//    }

    /**
     * deleteUser imi sterge si din microserviciu de Device si desi nu sterge device-u, ii pune fieldu pe null
     * */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable("id") int user_id) {
        try {

            //restTemplate.put("http://localhost:8081/device/removeUserFromDevices/" + user_id, null);
           restTemplate.put("http://device.localhost/device/removeUserFromDevices/" + user_id, null);
           // restTemplate.put("http://device_app:8081/device/removeUserFromDevices/" + user_id, null);

            LOGGER.info("Successfully removed user from devices in Device Microservice.");
        } catch (Exception e) {
            LOGGER.error("Failed to update devices in Device Microservice: " + e.getMessage());
        }

        // Acum ștergem utilizatorul din User MS
        int id = userService.deleteUser(user_id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


    @PutMapping(value = "/{id}")
    public ResponseEntity<Integer> updateUser(@PathVariable("id") int user_id, @RequestBody UserDTO userDTO) throws Exception {
       int id = userService.updateUser(user_id, userDTO);
       return new ResponseEntity<>(id, HttpStatus.OK);
    }




}
