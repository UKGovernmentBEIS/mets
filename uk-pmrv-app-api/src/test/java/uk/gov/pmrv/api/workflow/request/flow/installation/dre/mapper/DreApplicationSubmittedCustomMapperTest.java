package uk.gov.pmrv.api.workflow.request.flow.installation.dre.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;

@ExtendWith(MockitoExtension.class)
class DreApplicationSubmittedCustomMapperTest {

	@InjectMocks
	private DreApplicationSubmittedCustomMapper cut;

	@Test
    void toRequestActionDTO() {
		FileInfoDTO officialNotice = FileInfoDTO.builder().name("off").uuid(UUID.randomUUID().toString()).build();
    	RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.DRE_APPLICATION_SUBMITTED)
    			.payload(DreApplicationSubmittedRequestActionPayload.builder()
    					.dre(Dre.builder()
    	    					.officialNoticeReason("offi_notice")
    	    					.determinationReason(DreDeterminationReason.builder()
    	    							.operatorAskedToResubmit(true)
    	    							.regulatorComments("reg comments")
    	    							.build())
    	    					.build())
    					.officialNotice(officialNotice)
    	    			.build())
    			.build();
    	
    	RequestActionDTO result = cut.toRequestActionDTO(requestAction);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(DreApplicationSubmittedRequestActionPayload.class);
    	
    	DreApplicationSubmittedRequestActionPayload resultPayload = (DreApplicationSubmittedRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getDre().getDeterminationReason()).isNull();
    	assertThat(resultPayload.getDre().getOfficialNoticeReason()).isEqualTo("offi_notice");
    	assertThat(resultPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }
    
    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleType.OPERATOR, RoleType.VERIFIER);
    }
}
