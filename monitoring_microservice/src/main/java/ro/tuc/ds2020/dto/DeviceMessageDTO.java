package ro.tuc.ds2020.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // Asigură că există un constructor gol
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)  // Evită erori la deserializare dacă vin câmpuri extra
public class DeviceMessageDTO {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("description")
    private String description;

    @JsonProperty("address")
    private String address;

    @JsonProperty("maxHEC")
    private Double maxHEC;

    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("eventType")
    private String eventType;
}

