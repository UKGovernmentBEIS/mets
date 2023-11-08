package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class PermitVariationWaitForAmendsInitializerTest {

    @InjectMocks
    private PermitVariationWaitForAmendsInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void initializePayload() {
        final String requestId = "1";
        final Long accountId = 1L;

        PermitVariationRequestPayload permitRequestPayload = PermitVariationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_VARIATION_REQUEST_PAYLOAD)
            .build();

        Request request = Request.builder()
            .id(requestId)
            .accountId(accountId)
            .type(RequestType.PERMIT_VARIATION)
            .status(RequestStatus.IN_PROGRESS)
            .payload(permitRequestPayload)
            .build();

        InstallationOperatorDetails expectedInstallationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample").build();

        Mockito.when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
            .thenReturn(expectedInstallationOperatorDetails);
        RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_VARIATION_WAIT_FOR_AMENDS_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitVariationApplicationReviewRequestTaskPayload.class);

        PermitVariationApplicationReviewRequestTaskPayload permitVariationApplicationReviewRequestTaskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTaskPayload;

        assertThat(permitVariationApplicationReviewRequestTaskPayload.getInstallationOperatorDetails()).isEqualTo(expectedInstallationOperatorDetails);
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_VARIATION_WAIT_FOR_AMENDS));
    }
}
