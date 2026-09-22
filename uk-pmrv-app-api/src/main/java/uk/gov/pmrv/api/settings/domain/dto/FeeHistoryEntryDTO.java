package uk.gov.pmrv.api.settings.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import uk.gov.pmrv.api.settings.domain.enumeration.FeeHistoryActionType;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.FeeType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Getter
@Builder
@AllArgsConstructor
public class FeeHistoryEntryDTO {

    private LocalDateTime createdAt;
    private String changedBy;
    private FeeHistoryActionType actionType;
    private RequestType requestType;
    private FeeType feeType;
    private BigDecimal oldAmount;
    private BigDecimal newAmount;
    private LocalDate effectiveDate;
}
