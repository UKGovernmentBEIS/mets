package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

@ExtendWith(MockitoExtension.class)
class NerApplicationAddProceededToAuthorityRequestActionServiceTest {

    @InjectMocks
    private NerApplicationAddProceededToAuthorityRequestActionService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    

    @Test
    void execute() {
        
        final String requestId = "1";
        final String operator = "operator";
        final String signatory = "signatory";
        final Long accountId = 2L;
        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of(operator)).signatory(signatory).build();
        final NerRequestPayload requestPayload = NerRequestPayload.builder()
            .payloadType(RequestPayloadType.NER_REQUEST_PAYLOAD)
            .decisionNotification(decisionNotification)
            .regulatorAssignee("reviewer")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.NER)
            .payload(requestPayload)
            .accountId(accountId)
            .build();
        final NerApplicationProceededToAuthorityRequestActionPayload actionPayload = NerApplicationProceededToAuthorityRequestActionPayload.builder()
            .payloadType(RequestActionPayloadType.NER_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD)
            .decisionNotification(decisionNotification)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(Map.of());

        service.proceedToAuthority(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(request,
            actionPayload,
            RequestActionType.NER_APPLICATION_PROCEEDED_TO_AUTHORITY,
            "reviewer");
        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
    }
}
