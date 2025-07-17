package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service.InstallationOnsiteInspectionAddSubmittedRequestActionService;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionAddSubmittedRequestActionServiceTest {

    @InjectMocks
    private InstallationOnsiteInspectionAddSubmittedRequestActionService handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;


    @Test
    void add() {
        String requestId = "1";
        Set<String> operators = Set.of("oper");
        String signatory = "sign";
        DecisionNotification decisionNotification = DecisionNotification.builder()
                .signatory(signatory)
                .operators(operators)
                .build();

        InstallationInspection installationInspection = InstallationInspection.builder().build();

        final Request request = Request.builder()
                .id(requestId)
                .payload(InstallationInspectionRequestPayload.builder()
                        .decisionNotification(decisionNotification)
                        .regulatorAssignee("regulator")
                        .installationInspection(installationInspection)
                        .build())
                .build();

        Map<String, RequestActionUserInfo> usersInfo = Map.of("oper", RequestActionUserInfo.builder().name("operator1").build());

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver.getUsersInfo(operators, signatory, request))
                .thenReturn(usersInfo);

        InstallationInspectionApplicationSubmittedRequestActionPayload expectedActionPayload = InstallationInspectionApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.INSTALLATION_INSPECTION_APPLICATION_SUBMITTED_PAYLOAD)
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .installationInspection(installationInspection)
                .build();

        handler.add(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1)).getUsersInfo(operators, signatory, request);
        verify(requestService, times(1)).addActionToRequest(request, expectedActionPayload, RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED, "regulator");
    }
}
