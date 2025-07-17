package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountInfoDTO {

    private Long id;

    private String name;

    private CompetentAuthorityEnum competentAuthority;

    private String leName;

    private String emitterId;
}
