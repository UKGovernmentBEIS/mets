package uk.gov.pmrv.api.user.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorUserWithAuthorityDTO {

    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private Long userAuthorityId;
    private String accountName;
    private boolean enabled;
}
