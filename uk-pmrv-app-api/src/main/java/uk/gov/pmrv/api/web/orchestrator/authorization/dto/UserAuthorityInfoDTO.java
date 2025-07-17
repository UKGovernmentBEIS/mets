package uk.gov.pmrv.api.web.orchestrator.authorization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthorityInfoDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String roleName;
    private String roleCode;
    private LocalDateTime authorityCreationDate;
    private AuthorityStatus authorityStatus;
}
