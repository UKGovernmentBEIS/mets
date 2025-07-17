package uk.gov.pmrv.api.web.orchestrator.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AccountHeaderInfoDTO {

    private Long id;
    private String name;
}
