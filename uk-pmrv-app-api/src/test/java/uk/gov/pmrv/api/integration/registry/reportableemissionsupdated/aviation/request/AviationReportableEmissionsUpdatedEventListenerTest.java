package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;

@ExtendWith(MockitoExtension.class)
class AviationReportableEmissionsUpdatedEventListenerTest {

	@InjectMocks
	private AviationReportableEmissionsUpdatedEventListener cut;

	@Mock
	private AviationReportableEmissionsNotifyRegistryService aviationReportableEmissionsNotifyRegistryService;

	@Test
	void onReportableEmissionsUpdatedEvent() {
		AviationReportableEmissionsUpdatedEvent event = AviationReportableEmissionsUpdatedEvent.builder()
				.accountId(1L)
				.isFromDre(true)
				.reportableEmissions(BigDecimal.TEN)
				.year(Year.of(2000))
				.build();
		
		cut.onAviationReportableEmissionsUpdatedEvent(event);
		
		verify(aviationReportableEmissionsNotifyRegistryService, times(1)).notifyRegistry(event);
	}
	
}
