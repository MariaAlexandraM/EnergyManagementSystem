package ro.tuc.ds2020.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "measurement")
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "device_id", nullable = false)
    @JsonProperty("device_id")
    private int deviceId;

    @Column(name = "timestamp", nullable = false)
    @JsonProperty("timestamp")
    private Long timestamp;

    @Column(name = "measurement_value", nullable = false)
    @JsonProperty("measurement_value")
    private Double measurement_value;

    public Measurement(Long timestamp, int deviceId, Double measurementValue) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
        this.measurement_value = measurementValue;
    }
}