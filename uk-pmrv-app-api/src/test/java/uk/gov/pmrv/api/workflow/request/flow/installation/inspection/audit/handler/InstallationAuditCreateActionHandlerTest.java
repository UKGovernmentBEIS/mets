package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestMetadata;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationAuditCreateActionHandlerTest {

    @InjectMocks
    private InstallationAuditCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;


    @Test
    void process() {
        final String requestId = "IAU00001-1";
        final Long accountId = 1L;
        final Year year = Year.of(2024);

        final InstallationAuditRequestCreateActionPayload payload = InstallationAuditRequestCreateActionPayload.builder()
                .payloadType(RequestCreateActionPayloadType.INSTALLATION_AUDIT_REQUEST_CREATE_ACTION_PAYLOAD)
                .year(year)
                .build();
        final AppUser appUser = AppUser.builder().userId("regulator").build();

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.INSTALLATION_AUDIT)
                .accountId(accountId)
                .requestMetadata(InstallationInspectionRequestMetadata.builder()
                        .type(RequestMetadataType.INSTALLATION_INSPECTION)
                        .year(year)
                        .build())
                .requestPayload(InstallationAuditRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_AUDIT_REQUEST_PAYLOAD)
                        .auditYear(year)
                        .regulatorAssignee(appUser.getUserId())
                        .installationInspection(InstallationInspection.builder().details(InstallationInspectionDetails.builder().build()).build())
                        .build())
                .build();

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id(requestId).build());

        String result = handler.process(accountId, payload, appUser);

        assertThat(result).isEqualTo(requestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.INSTALLATION_AUDIT);
    }
}
