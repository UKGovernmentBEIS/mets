package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Year;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;

@ExtendWith(MockitoExtension.class)
class InstallationReportableEmissionsUpdatedEventListenerTest {

	@InjectMocks
	private InstallationReportableEmissionsUpdatedEventListener cut;

	@Mock
	private InstallationReportableEmissionsNotifyRegistryService installationReportableEmissionsNotifyRegistryService;

	@Test
	void onReportableEmissionsUpdatedEvent() {
		InstallationReportableEmissionsUpdatedEvent event = InstallationReportableEmissionsUpdatedEvent.builder()
				.accountId(1L)
				.isFromDre(true)
				.reportableEmissions(BigDecimal.TEN)
				.year(Year.of(2000))
				.build();
		
		cut.onInstallationReportableEmissionsUpdatedEvent(event);
		
		verify(installationReportableEmissionsNotifyRegistryService, times(1)).notifyRegistry(event);
	}
	
}
