package ro.tuc.ds2020.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.LoginDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.services.UserService;

import javax.validation.Valid;


@RestController
@CrossOrigin
@RequestMapping(value = "/login")
public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping()
//    public ResponseEntity<Integer> login(@Valid @RequestBody UserDTO userDTO) {
//        int id = userService.login(userDTO);
//        return new ResponseEntity<>(id, HttpStatus.OK);
//    }

    @PostMapping()
    public ResponseEntity<UserDTO> login(@Valid @RequestBody UserDTO userDTO) {
        UserDTO foundUser = userService.login(userDTO);
        return new ResponseEntity<>(foundUser, HttpStatus.OK);
    }


}
