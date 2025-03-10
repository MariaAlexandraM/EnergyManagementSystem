package ro.tuc.ds2020.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.services.DeviceService;
import ro.tuc.ds2020.services.UserService;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(value = "/device")
public class DeviceController {
    private final DeviceService deviceService;
    private final UserService userService;

    @Autowired
    public DeviceController(DeviceService deviceService, UserService userService) {
        this.deviceService = deviceService;
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> findAll() {
        List<DeviceDTO> dtos = deviceService.findAll();
        for(DeviceDTO dto : dtos) {
            Link deviceLink = linkTo(methodOn(DeviceController.class).getDevice(dto.getId())).withRel("deviceDetails");
            dto.add(deviceLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

//    @GetMapping()
//    public ResponseEntity<List<Device>> findAll() {
//        List<Device> devices = deviceService.getAllDevices();
//        return new ResponseEntity<>(devices, HttpStatus.OK);
//    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable("id") int id) {
        DeviceDTO dto = deviceService.findDeviceById(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<Integer> insertDevice(@Valid @RequestBody DeviceDTO deviceDTO) {
        int deviceid = deviceService.insertDevice(deviceDTO);
        return new ResponseEntity<>(deviceid, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Integer> updateDevice(@PathVariable("id") int id, @RequestBody DeviceDTO deviceDTO) {
        int deviceid = deviceService.updateDevice(id, deviceDTO);
        return new ResponseEntity<>(deviceid, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Integer> deleteDevice(@PathVariable("id") int id) {
        int deviceid = deviceService.deleteDevice(id);
        return new ResponseEntity<>(deviceid, HttpStatus.OK);
    }

    @GetMapping(value = "/user/{userid}/deviceList")
    public ResponseEntity<List<DeviceDTO>> getDeviceListForUser(@PathVariable("userid") int userid) {
        List<DeviceDTO> devices = deviceService.getDeviceListForUser(userid);
        return new ResponseEntity<>(devices, HttpStatus.OK);
    }

    @PostMapping(value = "/assignDevice/{deviceid}/user/{userid}")
    public ResponseEntity<Integer> assignDeviceToUser(@PathVariable("deviceid") int deviceid, @PathVariable("userid") int userid) {
        int id = deviceService.assignDeviceToUser(deviceid, userid);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @DeleteMapping(value = "/user/{userid}")
    public ResponseEntity<Integer> deleteDevicesByUser(@PathVariable("userid") int userid) {
        int id = deviceService.deleteDevicesByUser(userid);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @PutMapping(value = "/removeUserFromDevices/{userid}")
    public ResponseEntity<Integer> removeUserFromDevices(@PathVariable("userid") int userid) {
        int id = deviceService.removeUserFromDevices(userid);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }


}
