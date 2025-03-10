package ro.tuc.ds2020.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ro.tuc.ds2020.dto.DeviceDTO;

/**
 * client http care face requesturi spre Device
 * */
@Service
public class DeviceApiClient {

    private final RestTemplate restTemplate;

    public DeviceApiClient() {
        this.restTemplate = new RestTemplate();
    }

  //   private static final String DEVICE_SERVICE_URL = "http://localhost:8081/device/";
    private static final String DEVICE_SERVICE_URL = "http://device.localhost/device/";
    //private static final String DEVICE_SERVICE_URL = "http://device_app:8081/device/";


    // asta face requestu sa caute device u
    public DeviceDTO getDeviceById(Integer deviceId) {
        try {
            ResponseEntity<DeviceDTO> response = restTemplate.getForEntity(DEVICE_SERVICE_URL + deviceId, DeviceDTO.class);
           // ResponseEntity<DeviceDTO> response = restTemplate.getForEntity("http://device_app:8081/device/" + deviceId, DeviceDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while fetching device with ID " + deviceId + ": " + e.getMessage());
            return null;
        }
    }




}

