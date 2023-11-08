package uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
public class ReissueRequestPayload extends RequestPayload {
	
	private Integer consolidationNumber;

	private FileInfoDTO officialNotice;
    
    private FileInfoDTO document; //permit or emp
}
