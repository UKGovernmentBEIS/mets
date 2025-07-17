package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationDeterminedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitTransferADeterminedServiceTest {

    @InjectMocks
    private PermitTransferADeterminedService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void deemWithdrawn() {

        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final String primaryContact = "primaryContact";
        final String serviceContact = "serviceContact";
        final String regulatorReviewer = "regulatorReviewer";
        final String signatory = "signatory";
        final String primaryName = "primary name";
        final String serviceName = "service name";
        final String signatoryName = "signatory name";
        final FileInfoDTO officialNotice = FileInfoDTO.builder().name("officialNotice").build();
        
        final Request request = Request.builder()
            .id(requestId)
            .payload(PermitTransferARequestPayload.builder()
                .relatedRequestId(relatedRequestId)
                .officialNotice(officialNotice)
                .build())
            .build();
        final Request relatedRequest = Request.builder()
            .id(relatedRequestId)
            .payload(PermitTransferBRequestPayload.builder()
                .decisionNotification(DecisionNotification.builder().signatory(signatory).build())
                .regulatorReviewer(regulatorReviewer)
                .build())
            .build();

        final Map<String, RequestActionUserInfo> userInfoMap = Map.of(
            primaryContact, RequestActionUserInfo.builder().name(primaryName).build(),
            serviceContact, RequestActionUserInfo.builder().name(serviceName).build(),
            signatory, RequestActionUserInfo.builder().name(signatoryName).build()
        );

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestService.findRequestById(relatedRequestId)).thenReturn(relatedRequest);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(UserInfoDTO.builder().userId(primaryContact).build()));
        when(requestAccountContactQueryService.getRequestAccountContact(request, AccountContactType.SERVICE))
            .thenReturn(Optional.of(UserInfoDTO.builder().userId(serviceContact).build()));
        when(requestService.findRequestById(relatedRequestId)).thenReturn(relatedRequest);
        when(requestActionUserInfoResolver.getUsersInfo(Set.of(primaryContact, serviceContact), signatory, request))
            .thenReturn(userInfoMap);

        service.deemWithdrawn(requestId);

        verify(requestService, times(1)).addActionToRequest(
            request,
            PermitTransferAApplicationDeterminedRequestActionPayload.builder()
                .payloadType(
                    RequestActionPayloadType.PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
                .decisionNotification(DecisionNotification.builder()
                    .signatory(signatory)
                    .operators(Set.of(primaryContact, serviceContact))
                    .build())
                .officialNotice(officialNotice)
                .usersInfo(userInfoMap)
                .build(),
            RequestActionType.PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN,
            regulatorReviewer
        );
    }
}
