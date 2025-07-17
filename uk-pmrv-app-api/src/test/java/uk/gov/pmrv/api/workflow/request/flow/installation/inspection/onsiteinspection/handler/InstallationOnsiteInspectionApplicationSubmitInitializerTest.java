package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class InstallationOnsiteInspectionApplicationSubmitInitializerTest {

    @InjectMocks
    private InstallationOnsiteInspectionApplicationSubmitInitializer initializer;


    @Test
    void initializePayload() {
        InstallationOnsiteInspectionRequestPayload requestPayload = InstallationOnsiteInspectionRequestPayload.builder().build();
        Request request = Request.builder().payload(requestPayload).accountId(1L).build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload.class);
    }

    @Test
    void initializePayload_installationOnsiteInspectionExists_shouldInitializePayloadWithExistingOnsiteInspection() {
        final Long accountId = 1L;
        final InstallationInspection installationInspection = InstallationInspection.builder().build();

        final Request request = Request.builder()
                .type(RequestType.INSTALLATION_ONSITE_INSPECTION)
                .accountId(accountId)
                .payload(InstallationOnsiteInspectionRequestPayload.builder()
                        .installationInspection(installationInspection)
                        .build())
                .build();


        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(InstallationInspectionApplicationSubmitRequestTaskPayload.class);
        assertThat(requestTaskPayload).isEqualTo(InstallationInspectionApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT_PAYLOAD)
                .installationInspection(installationInspection)
                .build());
    }


    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT);
    }
}
