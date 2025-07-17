package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationReportableEmissionsUpdatedEventListener {

	private final InstallationReportableEmissionsNotifyRegistryService installationReportableEmissionsNotifyRegistryService;

	@EventListener(InstallationReportableEmissionsUpdatedEvent.class)
	public void onInstallationReportableEmissionsUpdatedEvent(InstallationReportableEmissionsUpdatedEvent event) {
		installationReportableEmissionsNotifyRegistryService.notifyRegistry(event);
	}

}
