package uk.gov.pmrv.api.workflow.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateInfo {

    private BigDecimal amount;
    private String paymentRefNum;
    private String description;
    private String returnUrl;
    private CompetentAuthorityEnum competentAuthority;
}
