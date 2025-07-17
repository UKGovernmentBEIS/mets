package uk.gov.pmrv.api.account.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountsVerificationBodyUnappointedEvent {

    private Set<Long> accountIds;
}
