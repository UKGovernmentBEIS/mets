package uk.gov.pmrv.api.workflow.request.application.taskview;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestMetadata;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestInfoDTO {

    private String id;
    private RequestType type;
    private CompetentAuthorityEnum competentAuthority;
    private Long accountId;
    private RequestMetadata requestMetadata;
    private Boolean paymentCompleted;
    private BigDecimal paymentAmount;

    public RequestInfoDTO(final String id, final RequestType type) {
        this.id = id;
        this.type = type;
    }
}
