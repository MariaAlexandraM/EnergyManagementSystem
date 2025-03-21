package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.entities.User;

public class UserBuilder {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(),
                user.getName(),
                user.getPassword(),
                user.getRole());
    }

    public static User toEntity(UserDTO userDTO) {
        return new User(userDTO.getId(),
                userDTO.getName(),
                userDTO.getPassword(),
                userDTO.getRole());
    }

}
