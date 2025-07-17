package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler.InstallationAuditApplicationPeerReviewInitializer;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class InstallationInspectionApplicationPeerReviewInitializerTest {

    @InjectMocks
    private InstallationAuditApplicationPeerReviewInitializer handler;

    @Test
    void initializePayload() {
        InstallationInspection installationInspection = InstallationInspection.builder().build();

        InstallationInspectionRequestPayload requestPayload = InstallationInspectionRequestPayload.builder()
                .installationInspection(installationInspection)
                .build();
        Request request = Request.builder().payload(requestPayload).build();

        RequestTaskPayload result = handler.initializePayload(request);

        assertThat(result.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertThat(result).isInstanceOf(InstallationInspectionApplicationSubmitRequestTaskPayload.class);
        assertThat(result).isEqualTo(InstallationInspectionApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_PAYLOAD).installationInspection(installationInspection).build());
    }
}
