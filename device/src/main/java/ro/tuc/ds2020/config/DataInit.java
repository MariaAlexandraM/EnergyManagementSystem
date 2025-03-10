package ro.tuc.ds2020.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.services.DeviceService;

@Component
@AllArgsConstructor
public class DataInit implements CommandLineRunner {

    private final DeviceService deviceService;
    @Override
    public void run(String... args) throws Exception {
        deviceService.syncUsersFromUserService();
        System.out.println("Users synced from User Service.");
        if (deviceService.findAll().isEmpty()) {
        //if (deviceService.getAllDevices().isEmpty()) {
            DeviceDTO device1 = new DeviceDTO("Smart Meter 1", "Str. Independentei 10", 120.1, -1);


            DeviceDTO device2 = new DeviceDTO("Smart Thermostat", "Bulevardul Unirii 20", 80.5, -1);
            DeviceDTO device3 = new DeviceDTO("Smart Light", "Calea București 45", 50.0, -1);

            deviceService.insertDevice(device1);
            System.out.println("device 1 id: " + device1.getId());
            deviceService.insertDevice(device2);
            deviceService.insertDevice(device3);

            System.out.println("Mock Device data has been seeded into the database.");
        }
    }
}