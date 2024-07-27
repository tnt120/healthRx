package com.healthrx.backend.kafka;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class KafkaReceiveModel {
    private String strategy;
    private List<String> emails;
    private String subject;
    private Map<String, String> data;
}
