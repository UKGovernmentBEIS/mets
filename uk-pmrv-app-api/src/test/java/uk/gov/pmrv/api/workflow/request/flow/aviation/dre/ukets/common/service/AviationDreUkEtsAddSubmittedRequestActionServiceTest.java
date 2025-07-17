package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDre;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreDeterminationReasonType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsAddSubmittedRequestActionServiceTest {

    @InjectMocks
    private AviationDreUkEtsAddSubmittedRequestActionService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Test
    void add_when_primary_contact_exists() {
        String requestId = "1";

        AviationDre dre = AviationDre.builder()
                .determinationReason(AviationDreDeterminationReason.builder()
                        .furtherDetails("details")
                        .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                        .build())
                .build();

        DecisionNotification decisionNotification = DecisionNotification.builder()
                .operators(Set.of("operator1", "operator2"))
                .signatory("signatory")
                .build();
        Map<String, RequestActionUserInfo> usersInfo = Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1UserName").build(),
                "operator2", RequestActionUserInfo.builder().name("operator2UserName").build(),
                "signatory", RequestActionUserInfo.builder().name("regulatorUserName").build()
        );
        final Request request = Request.builder()
                .id(requestId)
                .payload(AviationDreUkEtsRequestPayload.builder()
                        .regulatorAssignee("regulator")
                        .dre(dre)
                        .decisionNotification(decisionNotification)
                        .build())
                .build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("primaryContact").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));
        when(requestActionUserInfoResolver.getUsersInfo(Set.of("operator1", "operator2"), "signatory", request)).thenReturn(usersInfo);

        AviationDreApplicationSubmittedRequestActionPayload expectedActionPayload = AviationDreApplicationSubmittedRequestActionPayload
                .builder()
                .payloadType(RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
                .dre(dre)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .build();

        service.add(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload,
                RequestActionType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED, "regulator");
    }

    @Test
    void add_when_primary_contact_not_exists() {
        String requestId = "1";

        AviationDre dre = AviationDre.builder()
            .determinationReason(AviationDreDeterminationReason.builder()
                .furtherDetails("details")
                .type(AviationDreDeterminationReasonType.CORRECTING_NON_MATERIAL_MISSTATEMENT)
                .build())
            .build();

        String signatory = "signatory";
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .signatory(signatory)
            .build();
        RequestActionUserInfo signatoryUserInfo = RequestActionUserInfo.builder().name("signatoryName").build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(AviationDreUkEtsRequestPayload.builder()
                .regulatorAssignee("regulator")
                .dre(dre)
                .decisionNotification(decisionNotification)
                .build())
            .build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("primaryContact").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.empty());
        when(requestActionUserInfoResolver.getSignatoryUserInfo(signatory)).thenReturn(signatoryUserInfo);

        AviationDreApplicationSubmittedRequestActionPayload expectedActionPayload = AviationDreApplicationSubmittedRequestActionPayload
            .builder()
            .payloadType(RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD)
            .dre(dre)
            .decisionNotification(decisionNotification)
            .usersInfo(Map.of(signatory, signatoryUserInfo))
            .build();

        service.add(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload,
            RequestActionType.AVIATION_DRE_UKETS_APPLICATION_SUBMITTED, "regulator");
    }
}
