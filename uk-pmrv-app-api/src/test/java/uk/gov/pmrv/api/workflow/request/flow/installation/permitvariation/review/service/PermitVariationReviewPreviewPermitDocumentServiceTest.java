package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class PermitVariationReviewPreviewPermitDocumentServiceTest {

    @InjectMocks
    private PermitVariationReviewPreviewPermitDocumentService service;

    @Test
    void getType() {
        assertThat(service.getType()).isEqualTo(RequestTaskType.PERMIT_VARIATION_APPLICATION_REVIEW);
    }
}
