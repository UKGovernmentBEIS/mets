package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceRejectedAddRequestActionService;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedAddRequestActionServiceTest {

    @InjectMocks
    private PermitIssuanceRejectedAddRequestActionService service;

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
        final BigDecimal estimatedAnnualEmissions = BigDecimal.valueOf(50000);
        final Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(estimatedAnnualEmissions).build())
            .build();
        final PermitIssuanceRejectDetermination
            determination = PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of(operator)).signatory(signatory).build();
        final PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .permitType(PermitType.HSE)
            .permit(permit)
            .decisionNotification(decisionNotification)
            .determination(determination)
            .regulatorReviewer("reviewer")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.PERMIT_ISSUANCE)
            .payload(permitIssuanceRequestPayload)
            .accountId(accountId)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(Map.of());

        service.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
    }
}
