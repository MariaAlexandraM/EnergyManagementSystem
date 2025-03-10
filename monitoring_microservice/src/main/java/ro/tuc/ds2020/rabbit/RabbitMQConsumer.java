package ro.tuc.ds2020.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.dto.DeviceDTO;
import ro.tuc.ds2020.dto.DeviceMessageDTO;
import ro.tuc.ds2020.repository.MeasurementRepository;
import ro.tuc.ds2020.entities.Measurement;
import ro.tuc.ds2020.service.DeviceApiClient;

@Component
public class RabbitMQConsumer {

    @Autowired
    private MeasurementRepository measurementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceApiClient deviceApiClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "masuratori")
    public void receiveMasuratoriMessage(String message) {
        try {
            System.out.println("[OK] mesaj primit de la coada 'masuratori': " + message);

            // deserializare la mesaju primit
            Measurement data = new ObjectMapper().readValue(message, Measurement.class);
            measurementRepository.save(data);
            System.out.println("\tinserat in database: " + data + "\n-----------------------------------------");

            // extrag device_id-u primit prin masuroatoare si il caut in Device
            DeviceDTO device = deviceApiClient.getDeviceById(data.getDeviceId());

            if (device != null && data.getMeasurement_value() > device.getMaxHEC()) {
                String notification = "[!] consumption exceeded for Device " + data.getDeviceId() +
                        " | maxHEC: " + device.getMaxHEC() + " | read value: " + data.getMeasurement_value();

                System.out.println(notification);

                // trimiterea prin wewbsocket
                messagingTemplate.convertAndSend("/topic/alerts", notification);
            }

        } catch (Exception e) {
            System.err.println("[X] Eroare la procesarea mesajului din 'masuratori': " + message);
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "device_changes_queue")
    public void receiveDeviceChangesMessage(String message) {
        try {
            System.out.println("[OK] mesaj primit de la coada 'device_changes_queue': " + message);

            if (message.startsWith("\"") && message.endsWith("\"")) {
                message = objectMapper.readValue(message, String.class);
            }

            // deserializare de mesaj
            DeviceMessageDTO deviceMessage = objectMapper.readValue(message, DeviceMessageDTO.class);
            System.out.println("[OK] mesaj deserializat cu succes: " + deviceMessage);

        } catch (Exception e) {
            System.err.println("Eroare la procesarea mesajului din 'device_changes_queue': " + message);
            e.printStackTrace();
        }
    }
}
