package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.Year;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

@ExtendWith(MockitoExtension.class)
class InstallationReportableEmissionsSendToRegistryProducerTest {
	
	private InstallationReportableEmissionsSendToRegistryProducer cut;

	private final String topicName = "topicName";
	
	@BeforeEach
	void setup() {
		cut = new InstallationReportableEmissionsSendToRegistryProducer(topicName);
	}
	
    @Test
    void produce() {
    	Integer registryId = 1234567;
    	String reportableEmissions = "10";
    	Year reportingYear = Year.of(2000);
    	
    	AccountEmissionsUpdatedRequestEvent event = AccountEmissionsUpdatedRequestEvent.builder()
    			.registryId(registryId)
    			.reportableEmissions(reportableEmissions)
    			.reportingYear(reportingYear)
    			.build();
    	
    	KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
    	
    	cut.produce(event, kafkaTemplate);

		verify(kafkaTemplate, times(1)).send(topicName, String.valueOf(event.getRegistryId()), event);
    }

	@Test
	void testProduce_ThrowsBusinessExceptionWhenKafkaIsDown() {
		Integer registryId = 1234567;
		String reportableEmissions = "10";
		Year reportingYear = Year.of(2000);

		AccountEmissionsUpdatedRequestEvent event = AccountEmissionsUpdatedRequestEvent.builder().registryId(registryId)
				.reportableEmissions(reportableEmissions).reportingYear(reportingYear).build();
		
		KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> kafkaTemplate = Mockito.mock(KafkaTemplate.class);
		
		doThrow(new RuntimeException("Kafka is down")).when(kafkaTemplate).send(topicName,
				String.valueOf(event.getRegistryId()), event);
		
		BusinessException exception = assertThrows(BusinessException.class, () -> {
			cut.produce(event, kafkaTemplate);
		});
		
		assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_KAFKA_QUEUE_CONNECTION_ISSUE,
				exception.getErrorCode());
		assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_KAFKA_QUEUE_CONNECTION_ISSUE.getMessage(),
				exception.getMessage());
		assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_KAFKA_QUEUE_CONNECTION_ISSUE.getCode(),
				exception.getErrorCode().getCode());

		verify(kafkaTemplate, times(1)).send(topicName, String.valueOf(event.getRegistryId()), event);
	}
}