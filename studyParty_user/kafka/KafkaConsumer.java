package com.studyparty.studyparty_dubboapi.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "myTopic", groupId = "studyParty")
    public void listen(ConsumerRecord<?, ?> record) {
        System.out.printf("Received Message: topic=%s, partition=%d, offset=%d, key=%s, value=%s\n",
                record.topic(), record.partition(), record.offset(), record.key(), record.value());
    }
}