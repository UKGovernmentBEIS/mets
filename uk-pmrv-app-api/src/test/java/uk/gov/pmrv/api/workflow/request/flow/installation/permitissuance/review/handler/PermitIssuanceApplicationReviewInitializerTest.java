package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.handler.PermitIssuanceApplicationReviewInitializer;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceApplicationReviewInitializerTest {

    @InjectMocks
    private PermitIssuanceApplicationReviewInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void initializePayload() {
        final String requestId = "1";
        final Long accountId = 1L;

        PermitIssuanceRequestPayload permitRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .build();

        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .type(RequestType.PERMIT_ISSUANCE)
            .status(RequestStatus.IN_PROGRESS)
            .payload(permitRequestPayload)
            .build();

        InstallationOperatorDetails expectedInstallationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample").build();

        Mockito.when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
            .thenReturn(expectedInstallationOperatorDetails);

        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitIssuanceApplicationReviewRequestTaskPayload.class);

        PermitIssuanceApplicationReviewRequestTaskPayload permitIssuanceApplicationReviewRequestTaskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTaskPayload;

        assertThat(permitIssuanceApplicationReviewRequestTaskPayload.getInstallationOperatorDetails()).isEqualTo(expectedInstallationOperatorDetails);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW));
    }
}
