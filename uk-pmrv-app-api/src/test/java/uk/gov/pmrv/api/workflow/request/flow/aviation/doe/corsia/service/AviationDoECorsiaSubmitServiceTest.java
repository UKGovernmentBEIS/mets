package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsia;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaFee;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmitSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain.AviationDoECorsiaSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation.AviationDoECorsiaValidationService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationDoECorsiaSubmitServiceTest {

    @InjectMocks
    private AviationDoECorsiaSubmitService submitService;

    @Mock
    private AviationDoECorsiaValidationService validatorService;

    @Mock
    private DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Mock
    private AviationAccountQueryService aviationAccountQueryService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void applySaveAction() {
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                .build())
            .build();
        AviationDoECorsiaSubmitSaveRequestTaskActionPayload  taskActionPayload =
            AviationDoECorsiaSubmitSaveRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_DRE_UKETS_SAVE_APPLICATION_PAYLOAD)
                .doe(doe)
                .sectionCompleted(true)
                .build();

        AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .doe(AviationDoECorsia.builder().build())
            .sectionCompleted(false)
            .build();

        RequestTask requestTask = RequestTask.builder()
            .payload(requestTaskPayload)
            .build();

        //invoke
        submitService.applySaveAction(taskActionPayload, requestTask);

        AviationDoECorsiaApplicationSubmitRequestTaskPayload payloadSaved =
            (AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD, payloadSaved.getPayloadType());

        assertThat(((AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload()).getDoe())
                .isEqualTo(taskActionPayload.getDoe());

        assertThat(((AviationDoECorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload()).getSectionCompleted())
                .isEqualTo(taskActionPayload.getSectionCompleted());
    }

    @Test
    void applySubmitNotify() {
        UUID att1 = UUID.randomUUID();
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                .build())
            .fee(AviationDoECorsiaFee.builder().chargeOperator(false).build())
            .build();

        AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .doe(doe)
            .doeAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .accountId(1L)
                .payload(requestPayload)
                .build())
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory("signatory")
            .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        AviationAccountInfoDTO aviationAccountDTO = AviationAccountInfoDTO.builder()
            .location(LocationDTO.builder()
                .type(LocationType.ONSHORE)
                .build())
            .accountType(AccountType.AVIATION)
            .build();

        doNothing().when(validatorService).validateAviationDoECorsia(any(AviationDoECorsia.class));
        when(decisionNotificationUsersValidator.areUsersValid(any(RequestTask.class), any(DecisionNotification.class), any(AppUser.class))).thenReturn(true);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(1L)).thenReturn(aviationAccountDTO);

        submitService.applySubmitNotify(requestTask, decisionNotification, appUser);

        assertThat(requestPayload.getDoe()).isEqualTo(doe);
        assertThat(requestPayload.getSectionCompleted()).isTrue();
        assertThat(requestPayload.getDoeAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
    }

    @Test
    void applySubmitNotify_location_missing_throw_error() {
        UUID att1 = UUID.randomUUID();
        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .type(AviationDoECorsiaDeterminationReasonType.VERIFIED_EMISSIONS_REPORT_HAS_NOT_BEEN_SUBMITTED)
                .build())
            .fee(AviationDoECorsiaFee.builder().chargeOperator(false).build())
            .build();

        AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_SUBMIT_PAYLOAD)
            .doe(doe)
            .doeAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(true)
            .build();

        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .accountId(1L)
                .payload(requestPayload)
                .build())
            .payload(requestTaskPayload)
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory("signatory")
            .build();

        AppUser appUser = AppUser.builder().userId("user").build();

        AviationAccountInfoDTO aviationAccountDTO = AviationAccountInfoDTO.builder()
            .accountType(AccountType.AVIATION)
            .build();

        doNothing().when(validatorService).validateAviationDoECorsia(any(AviationDoECorsia.class));
        when(decisionNotificationUsersValidator.areUsersValid(any(RequestTask.class), any(DecisionNotification.class), any(AppUser.class))).thenReturn(true);
        when(aviationAccountQueryService.getAviationAccountInfoDTOById(1L)).thenReturn(aviationAccountDTO);

        assertThrows(
            BusinessException.class, () -> submitService.applySubmitNotify(requestTask, decisionNotification, appUser));
    }

    @Test
    void addSubmittedRequestAction_primary_contact_exists() {
        String requestId = "1";

        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("details")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1", "operator2"))
                .signatory("signatory")
                .build();

        final Request request = Request.builder()
            .id(requestId)
            .payload(AviationDoECorsiaRequestPayload.builder()
                .regulatorAssignee("regulator")
                .doe(doe)
                .decisionNotification(decisionNotification)
                .build())
            .build();

        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("primaryContact").build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1UserName").build(),
                "operator2", RequestActionUserInfo.builder().name("operator2UserName").build(),
                "signatory", RequestActionUserInfo.builder().name("regulatorUserName").build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operator1", "operator2"), "signatory", request)).thenReturn(usersInfo);

        AviationDoECorsiaSubmittedRequestActionPayload expectedActionPayload = AviationDoECorsiaSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.AVIATION_DOE_CORSIA_SUBMITTED_PAYLOAD)
            .doe(doe)
            .decisionNotification(decisionNotification)
            .usersInfo(usersInfo)
            .build();

        submitService.addSubmittedRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload,
            RequestActionType.AVIATION_DOE_CORSIA_SUBMITTED, "regulator");
    }

    @Test
    void addSubmittedRequestAction_primary_contact_does_not_exist() {
        String requestId = "1";

        AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("details")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .build();

        String signatory = "signatory";

        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory(signatory)
            .build();

        RequestActionUserInfo signatoryUserInfo = RequestActionUserInfo.builder().name("signatoryName").build();

        final Request request = Request.builder()
            .id(requestId)
            .payload(AviationDoECorsiaRequestPayload.builder()
                .regulatorAssignee("regulator")
                .doe(doe)
                .decisionNotification(decisionNotification)
                .build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(requestActionUserInfoResolver.getSignatoryUserInfo(signatory)).thenReturn(signatoryUserInfo);

        AviationDoECorsiaSubmittedRequestActionPayload expectedActionPayload = AviationDoECorsiaSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.AVIATION_DOE_CORSIA_SUBMITTED_PAYLOAD)
            .doe(doe)
            .decisionNotification(decisionNotification)
            .usersInfo(Map.of(signatory, signatoryUserInfo))
            .build();

        submitService.addSubmittedRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload,
            RequestActionType.AVIATION_DOE_CORSIA_SUBMITTED, "regulator");
    }

    @Test
    void requestPeerReview() {
        UUID att1 = UUID.randomUUID();
          AviationDoECorsia doe = AviationDoECorsia.builder()
            .determinationReason(AviationDoECorsiaDeterminationReason.builder()
                .furtherDetails("details")
                .type(AviationDoECorsiaDeterminationReasonType.CORRECTIONS_TO_A_VERIFIED_REPORT)
                .build())
            .build();


        AviationDoECorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationDoECorsiaApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW_PAYLOAD)
            .doe(doe)
            .doeAttachments(Map.of(att1, "atta1.pdf"))
            .sectionCompleted(false)
            .build();

        AviationDoECorsiaRequestPayload requestPayload = AviationDoECorsiaRequestPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .request(Request.builder()
                .payload(requestPayload)
                .build())
            .payload(requestTaskPayload).build();

        String peerReviewer = "peerReviewer";

        submitService.requestPeerReview(requestTask, peerReviewer);

        assertThat(requestPayload.getRegulatorPeerReviewer()).isEqualTo(peerReviewer);
        assertThat(requestPayload.getDoe()).isEqualTo(doe);
        assertThat(requestPayload.getSectionCompleted()).isFalse();
        assertThat(requestPayload.getDoeAttachments()).containsExactlyEntriesOf(Map.of(att1, "atta1.pdf"));
        assertThat(requestPayload.getPaymentAmount()).isNull();
    }
}
