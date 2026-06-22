package com.gindho.kafka;
import org.apache.kafka.clients.admin.NewTopic; import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration; import org.springframework.kafka.config.TopicBuilder;
@Configuration
public class KafkaConfig {
    @Bean public NewTopic patientTopic() { return TopicBuilder.name("patient").partitions(3).replicas(1).build(); }
    @Bean public NewTopic appointmentTopic() { return TopicBuilder.name("appointment").partitions(3).replicas(1).build(); }
    @Bean public NewTopic billingTopic() { return TopicBuilder.name("billing").partitions(3).replicas(1).build(); }
    @Bean public NewTopic notificationTopic() { return TopicBuilder.name("notification").partitions(3).replicas(1).build(); }
    @Bean public NewTopic auditTopic() { return TopicBuilder.name("audit").partitions(3).replicas(1).build(); }
    @Bean public NewTopic medicalTopic() { return TopicBuilder.name("medical").partitions(3).replicas(1).build(); }
    @Bean public NewTopic inventoryTopic() { return TopicBuilder.name("inventory").partitions(3).replicas(1).build(); }
    @Bean public NewTopic emergencyTopic() { return TopicBuilder.name("emergency").partitions(3).replicas(1).build(); }
    @Bean public NewTopic qualityTopic() { return TopicBuilder.name("quality").partitions(3).replicas(1).build(); }
    @Bean public NewTopic teleconsultationTopic() { return TopicBuilder.name("teleconsultation").partitions(3).replicas(1).build(); }
}