package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class PermitRevokedServiceTest {

    @InjectMocks
    private PermitRevokedService service;

    @Mock
    private InstallationAccountStatusService installationAccountStatusService;

    @Mock
    private RequestService requestService;

    @Test
    void applySavePayload() {

        final Request request = Request.builder()
            .id("id")
            .type(RequestType.PERMIT_REVOCATION)
            .accountId(1L)
            .build();

        when(requestService.findRequestById("id")).thenReturn(request);

        service.executePermitRevokedPostActions("id");

        verify(installationAccountStatusService, times(1)).handlePermitRevoked(1L);
    }

    @Test
    void constructAerVariables() {
        final String requestId = "1";
        LocalDate reportLocalDate = LocalDate.now();
        Date reportDate = DateUtils.atEndOfDay(reportLocalDate);

        PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
                .permitRevocation(PermitRevocation.builder()
                        .annualEmissionsReportRequired(true)
                        .annualEmissionsReportDate(reportLocalDate)
                        .build())
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);

        // Invoke
        Map<String, Object> result = service.constructAerVariables(requestId);

        // Verify
        assertThat(result).isEqualTo(Map.of(
                BpmnProcessConstants.AER_REQUIRED, true,
                BpmnProcessConstants.AER_EXPIRATION_DATE, reportDate
        ));
        verify(requestService).findRequestById(requestId);
    }
}