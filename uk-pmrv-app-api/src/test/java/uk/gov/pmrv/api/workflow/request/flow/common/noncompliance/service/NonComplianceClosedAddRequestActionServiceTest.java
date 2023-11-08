package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCloseJustification;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@ExtendWith(MockitoExtension.class)
class NonComplianceClosedAddRequestActionServiceTest {

    @InjectMocks
    private NonComplianceClosedAddRequestActionService service;

    @Mock
    private RequestService requestService;


    @Test
    void execute() {

        final String requestId = "1";
        final UUID file = UUID.randomUUID();
        final NonComplianceCloseJustification closeJustification = NonComplianceCloseJustification.builder()
            .reason("the reason")
            .files(Set.of(file))
            .build();
        final Map<UUID, String> files = Map.of(file, "filename");
        final String assignee = "regulatorAssignee";
        final NonComplianceRequestPayload requestPayload = NonComplianceRequestPayload.builder()
            .payloadType(RequestPayloadType.NON_COMPLIANCE_REQUEST_PAYLOAD)
            .closeJustification(closeJustification)
            .regulatorAssignee(assignee)
            .nonComplianceAttachments(files)
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .type(RequestType.NON_COMPLIANCE)
            .payload(requestPayload)
            .build();
        final NonComplianceApplicationClosedRequestActionPayload actionPayload =
            NonComplianceApplicationClosedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NON_COMPLIANCE_APPLICATION_CLOSED_PAYLOAD)
                .closeJustification(closeJustification)
                .nonComplianceAttachments(files)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        service.addRequestAction(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NON_COMPLIANCE_APPLICATION_CLOSED,
            assignee
        );
    }
}
