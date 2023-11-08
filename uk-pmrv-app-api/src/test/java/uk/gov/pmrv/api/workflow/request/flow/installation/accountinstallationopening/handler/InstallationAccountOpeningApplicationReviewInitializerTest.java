package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;

class InstallationAccountOpeningApplicationReviewInitializerTest {

    private final InstallationAccountOpeningApplicationReviewInitializer initializer =
        new InstallationAccountOpeningApplicationReviewInitializer();


    @Test
    void initializePayload() {
        final String requestId = "1";

        InstallationAccountOpeningRequestPayload installationAccountOpeningRequestPayload =
            InstallationAccountOpeningRequestPayload.builder()
                .payloadType(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD)
                .accountPayload(new InstallationAccountPayload())
                .build();

        Request request = Request.builder()
            .id(requestId)
            .type(RequestType.INSTALLATION_ACCOUNT_OPENING)
            .status(RequestStatus.IN_PROGRESS)
            .payload(installationAccountOpeningRequestPayload)
            .build();

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(InstallationAccountOpeningApplicationRequestTaskPayload.class);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes())
            .containsExactly(RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW);
    }
}
