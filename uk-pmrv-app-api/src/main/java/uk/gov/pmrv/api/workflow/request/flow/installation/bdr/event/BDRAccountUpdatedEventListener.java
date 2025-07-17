package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.event;

import com.google.common.base.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccountStatusChangedEvent;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service.BDRCreationService;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BDRAccountUpdatedEventListener {

    private final BDRCreationService bdrCreationService;
    private final ConfigurationService configurationService;
    private final LocalDate BDRReportingPeriodStart = LocalDate.of(2025, 4, 1); // April 1st 2025
    private final LocalDate BDRReportingPeriodEnd = LocalDate.of(2025, 6, 30); // June 30rd 2025
    private static final String BDR_REPORTABLE_PERIOD_TOGGLE_KEY = "bdr.reporting-period.toggle";


    @EventListener(InstallationAccountStatusChangedEvent.class)
    public void onBDRAccountUpdatedCreatedEvent(InstallationAccountStatusChangedEvent event) {
        if (Objects.equal(event.getStatus(), InstallationAccountStatus.LIVE)) {
            Optional<ConfigurationDTO> reportingPeriodActiveConfiguration = configurationService
                    .getConfigurationByKey(BDR_REPORTABLE_PERIOD_TOGGLE_KEY);

            boolean reportingPeriodActive = reportingPeriodActiveConfiguration
                    .map(configurationDTO -> Boolean.parseBoolean(configurationDTO.getValue().toString()))
                    .orElse(true);

            if ((reportingPeriodActive &&
                    (LocalDate.now().isEqual(BDRReportingPeriodStart) || LocalDate.now().isAfter(BDRReportingPeriodStart)) &&
                    (LocalDate.now().isEqual(BDRReportingPeriodEnd) || LocalDate.now().isBefore(BDRReportingPeriodEnd)))
                    ||
                    (!reportingPeriodActive && (LocalDate.now().isEqual(BDRReportingPeriodEnd) || LocalDate.now().isBefore(BDRReportingPeriodEnd)))) {

                bdrCreationService.createBDR(event.getAccountId());
            }
        }
    }
}
