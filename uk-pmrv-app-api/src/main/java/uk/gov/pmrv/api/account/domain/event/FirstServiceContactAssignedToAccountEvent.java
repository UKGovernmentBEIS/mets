package uk.gov.pmrv.api.account.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class FirstServiceContactAssignedToAccountEvent {

    private Long accountId;
    private ServiceContactDetails serviceContactDetails;
}
