package uk.gov.pmrv.api.reporting.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.reporting.domain.ReportableEmissionsUpdatedEvent;

@SuperBuilder
@Getter
@Setter
public class InstallationReportableEmissionsUpdatedEvent extends ReportableEmissionsUpdatedEvent {

    private String requestId;
    private boolean isSetOperatorId;
    private boolean isFromAerMarkedAsNotRequired;
}
