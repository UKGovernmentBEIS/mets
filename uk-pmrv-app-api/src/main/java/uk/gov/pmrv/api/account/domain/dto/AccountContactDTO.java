package uk.gov.pmrv.api.account.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountContactDTO {

    @NotNull(message = "{accountContact.accountId.notEmpty}")
    private Long accountId;

    private String userId;
}
