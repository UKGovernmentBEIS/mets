package uk.gov.pmrv.api.permit.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitContainer;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitEntityDto {

    private String id;
    private PermitContainer permitContainer;
    private Long accountId;
    private int consolidationNumber;
    private String fileDocumentUuid;
}
