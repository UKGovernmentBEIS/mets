package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;

class ReissueMapperTest {

	private final ReissueMapper cut = Mappers.getMapper(ReissueMapper.class);

    @Test
    void toCompletedActionPayload() {
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("off").uuid(UUID.randomUUID().toString())
    			.build();
    	FileInfoDTO permitDocument = FileInfoDTO.builder()
    			.name("permitDocument").uuid(UUID.randomUUID().toString())
    			.build();
    	ReissueRequestPayload requestPayload = ReissueRequestPayload.builder()
    			.payloadType(RequestPayloadType.REISSUE_REQUEST_PAYLOAD)
    			.officialNotice(officialNotice)
    			.document(permitDocument)
    			.build();
    	
    	ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.submitter("submitter")
    			.submitterId("submitterId")
    			.signatory("signatory")
    			.batchRequestId("permitBatchRequestId")
    			.build();
    	
		ReissueCompletedRequestActionPayload result = cut.toCompletedActionPayload(requestPayload, metadata,
				"signatoryName", RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD);
    	
    	assertThat(result).isEqualTo(ReissueCompletedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.PERMIT_REISSUE_COMPLETED_PAYLOAD)
    			.officialNotice(officialNotice)
    			.document(permitDocument)
    			.signatory("signatory")
    			.signatoryName("signatoryName")
    			.submitter("submitter")
    			.build());
    }
    
}
