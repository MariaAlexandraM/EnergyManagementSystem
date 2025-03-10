package ro.tuc.ds2020.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.Objects;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DeviceDTO extends RepresentationModel<DeviceDTO> {
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

    public DeviceDTO(String description, String address, double maxHEC, int userId) {
        this.description = description;
        this.address = address;
        this.maxHEC = maxHEC;
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDTO deviceDTO = (DeviceDTO) o;
        return description.equals(deviceDTO.description) &&
                address.equals(deviceDTO.address)
                && maxHEC == deviceDTO.maxHEC;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, address, maxHEC);
    }
}
