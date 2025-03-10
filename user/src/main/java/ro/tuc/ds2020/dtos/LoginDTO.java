package ro.tuc.ds2020.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO extends RepresentationModel<UserDTO> {
    private int id;
    private String name;
    private String password;

    public LoginDTO(String name, String password) {
        this.name = name;
        this.password = password;
    }

}