package uk.gov.pmrv.api.workflow.request.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestActionDTO {

    private Long id;
    
    private RequestActionType type;

    private RequestActionPayload payload;
    
    private String requestId;
    
    private RequestType requestType;
    
    private Long requestAccountId;
    
    private CompetentAuthorityEnum competentAuthority;

    private String submitter;

    private LocalDateTime creationDate;

}
