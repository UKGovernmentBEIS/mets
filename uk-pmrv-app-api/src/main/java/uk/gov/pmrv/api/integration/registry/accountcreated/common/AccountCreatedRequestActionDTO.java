package uk.gov.pmrv.api.integration.registry.accountcreated.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreatedRequestActionDTO {

    private String emitterId;
    private String permitId;
    private CompetentAuthorityEnum competentAuthority;


}
