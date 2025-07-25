package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationDeterminationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationOfficialNoticeType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.mapper.PermitCessationCompletedRequestActionPayloadMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitCessationNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD;

@ExtendWith(MockitoExtension.class)
class RequestPermitSurrenderCessationServiceTest {

    @InjectMocks
    private RequestPermitSurrenderCessationService requestPermitSurrenderCessationService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitCessationNotifyOperatorValidator cessationNotifyOperatorValidator;

    @Mock
    private PermitCessationCompletedRequestActionPayloadMapper cessationCompletedRequestActionPayloadMapper;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;
    
    @Mock
    private PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;

    @Test
    void applySaveCessation() {
        PermitCessation cessation = PermitCessation.builder()
            .determinationOutcome(PermitCessationDeterminationOutcome.APPROVED)
            .allowancesSurrenderDate(LocalDate.now())
            .numberOfSurrenderAllowances(10)
            .annualReportableEmissions(BigDecimal.valueOf(25000.25))
            .subsistenceFeeRefunded(false)
            .noticeType(PermitCessationOfficialNoticeType.NO_PROSPECT_OF_FURTHER_ALLOWANCES)
            .notes("notes")
            .build();
        PermitSaveCessationRequestTaskActionPayload taskActionPayload = PermitSaveCessationRequestTaskActionPayload.builder()
            .cessation(cessation)
            .cessationCompleted(true)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_SURRENDER_CESSATION_SUBMIT)
            .payload(PermitCessationSubmitRequestTaskPayload.builder()
                .payloadType(PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
                .cessationContainer(PermitCessationContainer.builder().allowancesSurrenderRequired(false).build())
                .cessationCompleted(false)
                .build())
            .build();

        requestPermitSurrenderCessationService.applySaveCessation(requestTask, taskActionPayload);

        assertThat(requestTask.getPayload()).isInstanceOf(PermitCessationSubmitRequestTaskPayload.class);

        PermitCessationSubmitRequestTaskPayload updatedRequestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(requestTask.getPayload().getPayloadType(), updatedRequestTaskPayload.getPayloadType());
        assertEquals(cessation, updatedRequestTaskPayload.getCessationContainer().getCessation());
        assertFalse(updatedRequestTaskPayload.getCessationContainer().isAllowancesSurrenderRequired());
        assertTrue(updatedRequestTaskPayload.getCessationCompleted());
    }

    @Test
    void executeNotifyOperatorActions() {
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .regulatorReviewer("regulatorReviewer")
                .build();
        Request request = Request.builder()
                .id("1")
            .type(RequestType.PERMIT_SURRENDER)
            .accountId(1L)
            .payload(requestPayload)
            .build();
        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(DecisionNotification.builder().build())
            .build();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload = PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
            .cessationContainer(PermitCessationContainer.builder()
                    .cessation(PermitCessation.builder().notes("notes").build())
                    .build())
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(11L)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId("processTaskId")
            .build();

        PermitCessationCompletedRequestActionPayload cessationCompletedRequestActionPayload =
            PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD)
                .build();
        AppUser appUser = AppUser.builder().userId("user").build();
        
        FileInfoDTO cessationOfficialNotice = FileInfoDTO.builder()
                .name("off notice").uuid(UUID.randomUUID().toString())
                .build();
        
        when(permitSurrenderOfficialNoticeService.generateCessationOfficialNotice(request, taskActionPayload.getDecisionNotification()))
            .thenReturn(cessationOfficialNotice);

        when(cessationCompletedRequestActionPayloadMapper.toCessationCompletedRequestActionPayload(requestTask, 
            taskActionPayload,
            cessationOfficialNotice,
            RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD))
            .thenReturn(cessationCompletedRequestActionPayload);

        //invoke
        requestPermitSurrenderCessationService.executeNotifyOperatorActions(requestTask, appUser, taskActionPayload);

        verify(cessationNotifyOperatorValidator, times(1))
            .validate(requestTask, appUser, taskActionPayload);
        verify(permitSurrenderOfficialNoticeService, times(1))
            .generateCessationOfficialNotice(request, taskActionPayload.getDecisionNotification());
        verify(installationAccountStatusService, times(1)).handleSurrenderCessationCompleted(request.getAccountId());
        verify(requestService, times(1))
            .addActionToRequest(request, cessationCompletedRequestActionPayload, RequestActionType.PERMIT_SURRENDER_CESSATION_COMPLETED, request.getPayload().getRegulatorReviewer());
        verify(permitSurrenderOfficialNoticeService, times(1))
            .sendOfficialNoticeForDecisionNotification(request, cessationOfficialNotice, taskActionPayload.getDecisionNotification());
        assertThat(requestPayload.getPermitCessation()).isEqualTo(PermitCessation.builder().notes("notes").build());
    }
}