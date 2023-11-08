package uk.gov.pmrv.api.workflow.request.flow.installation.dre.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFee;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSaveApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class DreApplyServiceTest {

	@InjectMocks
    private DreApplyService cut;
    
    @Mock
    private DreValidatorService dreValidatorService;
    
    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    @Test
    void applySaveAction() {
    	DreApplicationSubmitRequestTaskPayload taskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dreAttachments(new HashMap<>())
                .sectionCompleted(false)
                .build();

        RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

		DreSaveApplicationRequestTaskActionPayload taskActionPayload = DreSaveApplicationRequestTaskActionPayload
				.builder().payloadType(RequestTaskActionPayloadType.DRE_SAVE_APPLICATION_PAYLOAD)
				.dre(Dre.builder()
						.determinationReason(DreDeterminationReason.builder().regulatorComments("reg comments").build())
						.build())
				.sectionCompleted(true)
				.build();

        // Invoke
        cut.applySaveAction(taskActionPayload, requestTask);

        // Verify
        assertThat(requestTask.getPayload()).isInstanceOf(DreApplicationSubmitRequestTaskPayload.class);

        DreApplicationSubmitRequestTaskPayload payloadSaved = (DreApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        assertThat(payloadSaved.getDre()).isEqualTo(taskActionPayload.getDre());
        assertThat(payloadSaved.isSectionCompleted()).isEqualTo(taskActionPayload.isSectionCompleted());
    }
    
    @Test
    void applySubmitNotify() {
    	UUID att1 = UUID.randomUUID();
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.fee(DreFee.builder()
						.chargeOperator(true)
						.feeDetails(DreFeeDetails.builder()
								.hourlyRate(BigDecimal.TEN)
								.totalBillableHours(BigDecimal.TEN)
								.build())
						.build())
				.build();
    	DreApplicationSubmitRequestTaskPayload taskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dre(dre)
                .dreAttachments(Map.of(att1, "atta1.pdf"))
                .sectionCompleted(true)
                .build();
    	DreRequestPayload requestPayload = DreRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
        		.request(Request.builder()
        				.payload(requestPayload)
        				.build())
        		.payload(taskPayload).build();
    	
    	Set<String> operators = Set.of("oper");
    	String signatory = "sign";
    	DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory(signatory)
				.operators(operators)
				.build();
    	
    	PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
    	
    	when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser))
    		.thenReturn(true);

        // Invoke
        cut.applySubmitNotify(requestTask, decisionNotification, pmrvUser);
        
        verify(dreValidatorService, times(1)).validateDre(dre);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);
        assertThat(requestPayload.getDre()).isEqualTo(dre);
        assertThat(requestPayload.isSectionCompleted()).isEqualTo(true);
        assertThat(requestPayload.getDreAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getPaymentAmount()).isNull();
    }
    
    @Test
    void applySubmitNotify_decision_notification_users_not_valid() {
    	UUID att1 = UUID.randomUUID();
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.fee(DreFee.builder()
						.chargeOperator(true)
						.feeDetails(DreFeeDetails.builder()
								.hourlyRate(BigDecimal.TEN)
								.totalBillableHours(BigDecimal.TEN)
								.build())
						.build())
				.build();
    	DreApplicationSubmitRequestTaskPayload taskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dre(dre)
                .dreAttachments(Map.of(att1, "atta1.pdf"))
                .sectionCompleted(true)
                .build();
    	DreRequestPayload requestPayload = DreRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
        		.request(Request.builder()
        				.payload(requestPayload)
        				.build())
        		.payload(taskPayload).build();
    	
    	Set<String> operators = Set.of("oper");
    	String signatory = "sign";
    	DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory(signatory)
				.operators(operators)
				.build();
    	
    	PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
    	
    	when(decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser))
    		.thenReturn(false);

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> cut.applySubmitNotify(requestTask, decisionNotification, pmrvUser));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
        verify(dreValidatorService, times(1)).validateDre(dre);
        verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask, decisionNotification, pmrvUser);
    }
    
    @Test
    void requestPeerReview() {
    	String peerReviewer = "peerreviewer";
    	
    	UUID att1 = UUID.randomUUID();
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.fee(DreFee.builder()
						.chargeOperator(true)
						.feeDetails(DreFeeDetails.builder()
								.hourlyRate(BigDecimal.TEN)
								.totalBillableHours(BigDecimal.TEN)
								.build())
						.build())
				.build();
    	DreApplicationSubmitRequestTaskPayload requestTaskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dre(dre)
                .dreAttachments(Map.of(att1, "atta1.pdf"))
                .sectionCompleted(true)
                .build();
    	DreRequestPayload requestPayload = DreRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
        		.request(Request.builder()
        				.payload(requestPayload)
        				.build())
        		.payload(requestTaskPayload).build();
    	
        // Invoke
        cut.requestPeerReview(requestTask, peerReviewer);
        
        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(requestPayload.getDre()).isEqualTo(dre);
        assertThat(requestPayload.isSectionCompleted()).isEqualTo(true);
        assertThat(requestPayload.getDreAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
        assertThat(requestPayload.getPaymentAmount()).isNull();
    }
}
