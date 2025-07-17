package uk.gov.pmrv.api.integration.registry.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerProperties;

@Validated
@ConfigurationProperties(prefix = "kafka.aviation-producer")
@Getter
@Setter
public class AviationProducerConfigProperties extends NetzKafkaProducerProperties {

}