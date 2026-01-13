package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotifyErrorDTO {

    private String correlationId;
    private OperatorUpdateEventOutcome outcome;
    private OperatorUpdateEvent event;
    private String service;
    private CompetentAuthorityEnum authority;
    private String accountName;


}
