package uk.gov.pmrv.api.integration.registry.setoperator.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SetOperatorIdResponseEvent {

    private String emitterId;
    private Integer operatorId;
    private CompetentAuthorityEnum regulator;
}
