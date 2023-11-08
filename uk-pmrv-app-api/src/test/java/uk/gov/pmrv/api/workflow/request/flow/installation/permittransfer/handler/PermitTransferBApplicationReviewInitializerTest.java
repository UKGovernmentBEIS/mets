package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler.PermitTransferBApplicationReviewInitializer;

@ExtendWith(MockitoExtension.class)
class PermitTransferBApplicationReviewInitializerTest {

    @InjectMocks
    private PermitTransferBApplicationReviewInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void initializePayload() {

        final long accountId = 1L;
        final PermitTransferDetails permitTransferDetails = PermitTransferDetails.builder().reason("reason").build();
        final Request request = Request.builder()
            .accountId(accountId)
            .payload(PermitTransferBRequestPayload.builder()
                .permitTransferDetails(permitTransferDetails)
                .build())
            .build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installationName").build();
        
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);    
        
        final PermitTransferBApplicationReviewRequestTaskPayload result = 
            (PermitTransferBApplicationReviewRequestTaskPayload) initializer.initializePayload(request);

        assertEquals(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD, result.getPayloadType());
        assertEquals(installationOperatorDetails, result.getInstallationOperatorDetails());
        assertEquals(result.getPermitTransferDetails(), permitTransferDetails);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW);
    }
}
