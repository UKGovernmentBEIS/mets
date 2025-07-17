package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class EmpVariationUkEtsReviewPreviewEmpDocumentServiceTest {

    @InjectMocks
    private EmpVariationUkEtsReviewPreviewEmpDocumentService service;

    @Test
    void getType() {
        assertEquals(service.getTypes(), List.of(RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_REVIEW));
    }
}
