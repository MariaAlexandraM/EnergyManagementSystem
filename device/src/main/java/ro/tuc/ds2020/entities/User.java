package ro.tuc.ds2020.entities;

import lombok.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "user_in_device")
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userid", nullable = false)
    private int userid; // ala ce il iau din microserviciu de user

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Device> deviceList;

    public User(int userid, List<Device> deviceList) {
        this.userid = userid;
        this.deviceList = deviceList;
    }

    public User(int userid) {
        this.userid = userid;
    }

    public User(int id, int userid) {
        this.id = id;
        this.userid = userid;
    }

}
