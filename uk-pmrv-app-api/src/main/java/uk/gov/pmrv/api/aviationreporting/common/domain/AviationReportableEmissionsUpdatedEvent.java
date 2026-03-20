package uk.gov.pmrv.api.aviationreporting.common.domain;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.reporting.domain.ReportableEmissionsUpdatedEvent;

@SuperBuilder
@Getter
public class AviationReportableEmissionsUpdatedEvent extends ReportableEmissionsUpdatedEvent {

    private String requestId;
}
