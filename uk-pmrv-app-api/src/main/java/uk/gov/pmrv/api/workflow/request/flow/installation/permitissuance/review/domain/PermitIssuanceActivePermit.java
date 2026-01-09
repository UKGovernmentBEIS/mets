package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitIssuanceActivePermit {

    private String emitterId;
    private String permitId;
    private String installationName;
    private String operatorName;
    private CompetentAuthorityEnum regulator;
    private LocalDate regulatedActivitiesStartDate;

}
