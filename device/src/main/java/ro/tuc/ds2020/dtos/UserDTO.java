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
public class UserDTO extends RepresentationModel<UserDTO> {
    private int id;
    private int userid; // ala ce il iau din microserviciu de user

    public UserDTO(int userid) {
        this.userid = userid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return userid == userDTO.userid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid);
    }
}
