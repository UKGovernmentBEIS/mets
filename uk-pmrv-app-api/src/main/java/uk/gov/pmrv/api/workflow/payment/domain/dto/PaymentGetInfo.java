package uk.gov.pmrv.api.workflow.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentGetInfo {

    private String paymentId;
    private CompetentAuthorityEnum competentAuthority;
}
