package uk.gov.pmrv.api.account.installation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class InstallationAccountStatusChangedEvent {

    private Long accountId;
    private InstallationAccountStatus status;
}
