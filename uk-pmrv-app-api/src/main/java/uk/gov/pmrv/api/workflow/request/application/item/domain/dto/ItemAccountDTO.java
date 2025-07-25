package uk.gov.pmrv.api.workflow.request.application.item.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemAccountDTO {

    private Long accountId;

    private String accountName;

    private CompetentAuthorityEnum competentAuthority;

    private String leName;
}
