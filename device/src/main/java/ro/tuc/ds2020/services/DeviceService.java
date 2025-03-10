package ro.tuc.ds2020.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.config.RabbitMQConfig;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceMessageDTO;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.builders.DeviceBuilder;
import ro.tuc.ds2020.entities.Device;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.repositories.DeviceRepository;
import ro.tuc.ds2020.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;


    //private String USER_SERVICE_URL = "http://localhost:8080";
    private String USER_SERVICE_URL = "http://user.localhost";
    //private static final String USER_SERVICE_URL = "http://user_app:8080/user";


    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, RestTemplate restTemplate, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    private void publishDeviceChangeEvent(Device device, String eventType) {
        try {
            DeviceMessageDTO messageDTO = new DeviceMessageDTO(
                    device.getId(),
                    device.getDescription(),
                    device.getAddress(),
                    device.getMaxHEC(),
                    device.getUser() != null ? device.getUser().getUserid() : null,
                    eventType
            );

            // serializar de mesaj
            String messageAsJson = objectMapper.writeValueAsString(messageDTO);
            LOGGER.info("Sending message to RabbitMQ: {}", messageAsJson);

            // trimit mesaju spre coada de device_changes
            rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_QUEUE, messageAsJson);
            System.out.println("\ttrimis mesaj spre rabbit: \n\t\t" + messageAsJson);
            LOGGER.info("Message successfully sent to RabbitMQ queue: {}", RabbitMQConfig.DEVICE_QUEUE);
        } catch (Exception e) {
            LOGGER.error("Failed to publish device change event", e);
        }
    }

    public List<DeviceDTO> findAll() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

//    public List<Device> getAllDevices() {
//        return deviceRepository.findAll();
//    }

    // read
    public DeviceDTO findDeviceById(int id) {
        Optional<Device> device = deviceRepository.findById(id);
        if(!device.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDTO(device.get());
    }

    // create
    public int insertDevice(DeviceDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        System.out.println("\t- inserat device: " + device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());

        // trimit evenimentul de "CREATE" pe coada de updates
        publishDeviceChangeEvent(device, "CREATE");

        return device.getId();
    }

    // update
    @Transactional
    public int updateDevice(int id, DeviceDTO deviceDTO){
        Optional<Device> optionalDevice = deviceRepository.findById(id);
        if (optionalDevice.isPresent()) {
            Device device = optionalDevice.get();

            if (!device.getDescription().equals(deviceDTO.getDescription())) {
                device.setDescription(deviceDTO.getDescription());
            }
            if (!device.getAddress().equals(deviceDTO.getAddress())) {
                device.setAddress(deviceDTO.getAddress());
            }
            if (device.getMaxHEC() != deviceDTO.getMaxHEC()) {
                device.setMaxHEC(deviceDTO.getMaxHEC());
            }

            if (deviceDTO.getUserId() != null && deviceDTO.getUserId() > 0) {
                Optional<User> userOptional = userRepository.findById(deviceDTO.getUserId());
                if (userOptional.isPresent()) {
                    device.setUser(userOptional.get());
                } else {
                    LOGGER.warn("User with id {} was not found, device user not updated.", deviceDTO.getUserId());
                }
            }

            Device updatedDevice = deviceRepository.save(device);


            Integer userId = (updatedDevice.getUser() != null) ? updatedDevice.getUser().getUserid() : null;

            // trimit evenimentul de "UPDATE" pe coada de updates
            publishDeviceChangeEvent(updatedDevice, "UPDATE");

            return updatedDevice.getId();
        } else {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
    }


    // delete
   @Transactional
   public int deleteDevice(int id) {
       Optional<Device> optionalDevice = deviceRepository.findById(id);
       if (optionalDevice.isPresent()) {
           Device device = optionalDevice.get();

           User user = device.getUser();

           if (user != null) {
               user.getDeviceList().remove(device);
               device.setUser(null);
               userRepository.save(user);
           }

           deviceRepository.deleteById(id);

           // trimit evenimentul de "DELETE" pe coada de updates
           publishDeviceChangeEvent(device, "DELETE");

           return id;
       } else {
           LOGGER.error("Device with id {} was not found in db", id);
           throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
       }
   }

    public List<DeviceDTO> getDeviceListForUser(int user_id) {
        Optional<User> userOptional = userRepository.findById(user_id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", user_id);
            throw new  ResourceNotFoundException(User.class.getSimpleName() + " with id: " + user_id);
        }
        List<Device> deviceListForUser = userOptional.get().getDeviceList();
        return deviceListForUser.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    public int assignDeviceToUser(int device_id, int user_id) {
        Optional<User> userOptional = userRepository.findById(user_id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", user_id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + user_id);
        }
        Optional<Device> deviceOptional = deviceRepository.findById(device_id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", device_id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + device_id);
        }
        User user = userOptional.get();
        Device device = deviceOptional.get();

        device.setUser(user);

        if (!user.getDeviceList().contains(device)) {
            user.getDeviceList().add(device);
        }

        deviceRepository.save(device);
        userRepository.save(user);
        return user.getId();
    }

    @Transactional
    public int deleteDevicesByUser(int userId) {
        List<Device> devices = deviceRepository.findByUserId(userId);

        if (!devices.isEmpty()) {
            deviceRepository.deleteAll(devices);
            LOGGER.info("Deleted all devices for user with ID: {}", userId);
        } else {
            LOGGER.warn("No devices found for user with ID: {}", userId);
        }
        return userId;
    }

    @Transactional
    public int removeUserFromDevices(int userId) {
        List<Device> devices = deviceRepository.findByUserId(userId);

        if (!devices.isEmpty()) {
            for (Device device : devices) {
                device.setUser(null);
                deviceRepository.save(device);
            }
            LOGGER.info("User ID {} removed from all devices", userId);
        } else {
            LOGGER.warn("No devices found for user with ID: {}", userId);
        }
        return userId;
    }


    public void syncUsersFromUserService() {
        String url = USER_SERVICE_URL + "/user"; // GET request sa mi dea toti userii

        System.out.println("\n--------------------------------------------------------------------------\n\t\tCalling URL: " + url + "\n--------------------------------------------------------------------------");

        try {
            // aici comunica microserviciu de Device cu ala de User
            ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(url, UserDTO[].class);
            System.out.println("\n--------------------------------------------------------------------------\n\t\tResponse: " + response.getStatusCode() + "\n--------------------------------------------------------------------------");

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<UserDTO> usersFromUserService = Arrays.asList(response.getBody());

                for (UserDTO userDTO : usersFromUserService) {
                    if (!userRepository.existsByUserid(userDTO.getId())) {
                        User newUser = new User(userDTO.getId());
                        userRepository.save(newUser);
                    }
                }
                LOGGER.info("User synchronization completed successfully.");
            } else {
                LOGGER.error("Failed to sync users from UserService.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
