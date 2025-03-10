package ro.tuc.ds2020.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {

    // coada pt masuratori
    public static final String MEASUREMENTS_QUEUE = "masuratori";

   // pt creare de topic exchange
    public static final String MEASUREMENTS_QUEUE_EXCHANGE = "masuratori_exchange";
    public static final String MEASUREMENTS_QUEUE_ROUTING_KEY = "masuratori.#";

    // coada pt updates
    public static final String DEVICE_CHANGES_QUEUE = "device_changes_queue";

    @Bean
    public Queue masuratoriQueue() {
        return new Queue(MEASUREMENTS_QUEUE, false);
    }

    @Bean
    public TopicExchange masuratoriExchange() {
        return new TopicExchange(MEASUREMENTS_QUEUE_EXCHANGE, true, false);
    }

    // binds the queue (masuratori) la topic folosinf the routing key
    @Bean
    public Binding masuratoriBinding() {
        return BindingBuilder.bind(masuratoriQueue())
                .to(masuratoriExchange())
                .with(MEASUREMENTS_QUEUE_ROUTING_KEY);
    }

    // defineste coada rabbit, si ii durable adica mesajele nu se pierd la restart
    @Bean
    public Queue deviceChangesQueue() {
        return new Queue(DEVICE_CHANGES_QUEUE, true);
    }
}
