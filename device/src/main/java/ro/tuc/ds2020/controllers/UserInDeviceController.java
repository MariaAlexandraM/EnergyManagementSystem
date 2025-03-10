package ro.tuc.ds2020.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.services.UserService;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/user_in_device")
public class UserInDeviceController {
    private final UserService userService;

    @Autowired
    public UserInDeviceController(UserService userService) {
        this.userService = userService;
    }


//    @PostMapping
//    public ResponseEntity<Void> addUserToDeviceService(@RequestBody UserDTO userDTO) {
//        Optional<User> existingUser = userService.findByUserid(userDTO.getUserid());
//        if (existingUser.isPresent()) {
//            return new ResponseEntity<>(HttpStatus.CONFLICT);
//        }
//
//        User newUser = new User(userDTO.getUserid());
//        userService.insertUser(newUser);
//        System.out.println("User with id " + userDTO.getUserid() + " was inserted in db");
//
//        return new ResponseEntity<>(HttpStatus.CREATED);
//    }




    @PostMapping
    public ResponseEntity<Void> addUserToDeviceService(@RequestBody UserDTO userDTO) {
        if (userDTO.getUserid() == 0) { // dc userid e 0, il setam la id u primit
            userDTO.setUserid(userDTO.getId());
        }

        Optional<User> existingUser = userService.findByUserid(userDTO.getUserid());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        User newUser = new User(userDTO.getUserid());
        userService.insertUser(newUser);

        System.out.println("User with id " + newUser.getId() + " and userid " + userDTO.getUserid() + " inserted in Device Microservice");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }



    @DeleteMapping(value = "/{userid}")
    public ResponseEntity<Void> deleteUserFromDeviceService(@PathVariable("userid") int userid) {
        Optional<User> user = userService.findByUserid(userid);
        if (user.isPresent()) {
            userService.deleteUser(user.get().getId());
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }















    @GetMapping()
    public ResponseEntity<List<UserDTO>> findAll() {
        List<UserDTO> dtos = userService.findAll();
        for(UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserInDeviceController.class).getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable("id") int id) {
        UserDTO dto = userService.findUserById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
//
//    @PostMapping()
//    public ResponseEntity<Integer> insertUser(@Valid @RequestBody UserDTO userDTO) {
//        int id = userService.insertUserDTO(userDTO);
//        return new ResponseEntity<>(id, HttpStatus.CREATED);
//    }
//
//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Integer> deleteUser(@PathVariable("id") int userid) {
//        int id = userService.deleteUser(userid);
//        return new ResponseEntity<>(id, HttpStatus.OK);
//    }



}
