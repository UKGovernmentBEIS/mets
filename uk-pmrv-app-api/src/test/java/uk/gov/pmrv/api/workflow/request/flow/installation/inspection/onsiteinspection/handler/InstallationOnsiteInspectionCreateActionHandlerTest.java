package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestCreateActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionCreateActionHandlerTest {

    @InjectMocks
    private InstallationOnsiteInspectionCreateActionHandler handler;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private DateService dateService;


    @Test
    void process() {
        final String requestId = "IOI00001-1";
        final Long accountId = 1L;
        final Year year = Year.of(Year.now().getValue());
        final RequestCreateActionEmptyPayload payload = RequestCreateActionEmptyPayload.builder().payloadType(RequestCreateActionPayloadType.EMPTY_PAYLOAD).build();

        final AppUser appUser = AppUser.builder().userId("regulator").build();

        final RequestParams requestParams = RequestParams.builder()
                .type(RequestType.INSTALLATION_ONSITE_INSPECTION)
                .accountId(accountId)
                .requestMetadata(InstallationInspectionRequestMetadata.builder()
                        .type(RequestMetadataType.INSTALLATION_INSPECTION)
                        .year(year)
                        .build())
                .requestPayload(InstallationInspectionRequestPayload.builder()
                        .payloadType(RequestPayloadType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PAYLOAD)
                        .regulatorAssignee(appUser.getUserId())
                        .installationInspection(InstallationInspection.builder().details(InstallationInspectionDetails.builder().build()).build())
                        .build())
                .build();

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(Request.builder().id(requestId).build());

        when(dateService.getYear())
                .thenReturn(Year.now());

        String result = handler.process(accountId, payload, appUser);

        assertThat(result).isEqualTo(requestId);
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void getRequestCreateActionType() {
        assertThat(handler.getRequestCreateActionType()).isEqualTo(RequestCreateActionType.INSTALLATION_ONSITE_INSPECTION);
    }
}
