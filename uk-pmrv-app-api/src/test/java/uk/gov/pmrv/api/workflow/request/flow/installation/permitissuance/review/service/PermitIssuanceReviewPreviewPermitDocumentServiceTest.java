package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class PermitIssuanceReviewPreviewPermitDocumentServiceTest {
    @InjectMocks
    private PermitIssuanceReviewPreviewPermitDocumentService service;

    @Test
    void getType() {
        assertThat(service.getType()).isEqualTo(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW);
    }
}
