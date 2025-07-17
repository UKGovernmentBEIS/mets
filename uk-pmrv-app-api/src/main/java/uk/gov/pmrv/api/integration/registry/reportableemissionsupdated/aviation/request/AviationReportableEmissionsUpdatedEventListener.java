package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
class AviationReportableEmissionsUpdatedEventListener {

	private final AviationReportableEmissionsNotifyRegistryService aviationReportableEmissionsNotifyRegistryService;

	@EventListener(AviationReportableEmissionsUpdatedEvent.class)
	public void onAviationReportableEmissionsUpdatedEvent(AviationReportableEmissionsUpdatedEvent event) {
		aviationReportableEmissionsNotifyRegistryService.notifyRegistry(event);
	}

}
