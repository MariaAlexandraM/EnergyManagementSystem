package ro.tuc.ds2020.entities;

import lombok.*;
import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "device")
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "maxHEC", nullable = false)
    private Double maxHEC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    public Device(String description, String address, Double maxHEC) {
        this.description = description;
        this.address = address;
        this.maxHEC = maxHEC;
    }

    public Device(Integer id, String description, String address, Double maxHEC) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHEC = maxHEC;
    }
}
