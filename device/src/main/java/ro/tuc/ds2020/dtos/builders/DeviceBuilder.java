package ro.tuc.ds2020.dtos.builders;

import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;


public class DeviceBuilder {
    public static DeviceDTO toDeviceDTO(Device device) {
        Integer userId = (device.getUser() != null) ? device.getUser().getId() : null;
        return new DeviceDTO(device.getId(),
                device.getDescription(),
                device.getAddress(),
                device.getMaxHEC(),
                userId);
    }

    public static Device toEntity(DeviceDTO deviceDTO) {
        return new Device(
                deviceDTO.getId(),
                //null,
                deviceDTO.getDescription(),
                deviceDTO.getAddress(),
                deviceDTO.getMaxHEC());
    }


}
