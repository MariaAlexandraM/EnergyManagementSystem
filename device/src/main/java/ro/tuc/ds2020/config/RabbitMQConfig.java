package ro.tuc.ds2020.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * coada ce ma notifica despre schimbarile din device
     * */
    public static final String DEVICE_QUEUE = "device_changes_queue";

    /**
     * defineste coada rabbit, si ii durable adica mesajele nu se pierd la restart
     * */
    @Bean
    public Queue deviceQueue() {
        return new Queue(DEVICE_QUEUE, true);
    }

    /**
     * pt conversia json la mesaje
     * */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * template u care trimite mesajele in coada, in fromatu json
     * */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}