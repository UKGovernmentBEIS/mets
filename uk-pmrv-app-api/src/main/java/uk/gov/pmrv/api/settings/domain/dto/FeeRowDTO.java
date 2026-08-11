package uk.gov.pmrv.api.settings.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Getter
@Builder
@AllArgsConstructor
public class FeeRowDTO {

    private Long id;
    private RequestType requestType;
    private FeeType feeType;
    private BigDecimal amount;
    private BigDecimal scheduledAmount;
    private LocalDate scheduledDate;
}
