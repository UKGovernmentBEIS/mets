package uk.gov.pmrv.api.reporting.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.reporting.domain.ReportableEmissionsUpdatedEvent;

@SuperBuilder
@Getter
public class InstallationReportableEmissionsUpdatedEvent extends ReportableEmissionsUpdatedEvent {
}
