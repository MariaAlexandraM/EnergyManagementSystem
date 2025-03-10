package ro.tuc.ds2020.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO extends RepresentationModel<UserDTO> {
    private int id;
    private String name;
    private String password;
    private String role;

    public UserDTO(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public UserDTO(int id) {
        this.id = id;
    }

    public UserDTO(int id, int id1) {
       // this.id = id;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(getName(), userDTO.getName()) &&
                Objects.equals(getPassword(), userDTO.getPassword()) &&
                Objects.equals(getRole(), userDTO.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getPassword(), getRole());
    }
}
