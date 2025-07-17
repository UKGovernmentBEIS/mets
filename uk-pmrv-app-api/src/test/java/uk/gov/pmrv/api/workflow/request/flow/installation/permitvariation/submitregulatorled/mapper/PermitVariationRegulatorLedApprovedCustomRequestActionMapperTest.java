package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedApprovedCustomRequestActionMapperTest {

	@InjectMocks
	private PermitVariationRegulatorLedApprovedCustomRequestActionMapper cut;

    @Test
    void toRequestActionDTO() {
    	RequestAction requestAction = RequestAction.builder()
    			.type(RequestActionType.PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED)
    			.payload(PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.builder()
    					.determination(PermitVariationRegulatorLedGrantDetermination.builder()
    							.reason("reason")
    							.activationDate(LocalDate.now().plusDays(1))
    							.build())
    					.decisionNotification(DecisionNotification.builder()
    							.signatory("sign")
    							.build())
    					.officialNotice(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("offnotice").build())
    					.permitDocument(FileInfoDTO.builder().uuid(UUID.randomUUID().toString()).name("permitdoc").build())
    					.build())
    			.build();
    	
    	RequestActionDTO result = cut.toRequestActionDTO(requestAction);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getType()).isEqualTo(requestAction.getType());
    	assertThat(result.getPayload()).isInstanceOf(PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.class);
    	
    	PermitVariationApplicationRegulatorLedApprovedRequestActionPayload resultPayload = (PermitVariationApplicationRegulatorLedApprovedRequestActionPayload) result.getPayload();
    	assertThat(resultPayload.getDetermination().getReason()).isNull();
    	assertThat(resultPayload.getDetermination().getActivationDate()).isEqualTo(LocalDate.now().plusDays(1));
    }
    
    @Test
    void getUserRoleTypes() {
    	assertThat(cut.getUserRoleTypes()).containsExactlyInAnyOrder(RoleTypeConstants.OPERATOR, RoleTypeConstants.VERIFIER);
    }
}
