package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
public class EmpVariationCorsiaReviewPreviewEmpDocumentServiceTest {


    @InjectMocks
    private EmpVariationCorsiaReviewPreviewEmpDocumentService service;

    @Test
    void getType() {
        assertEquals(service.getTypes(), List.of(RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_REVIEW));
    }
}
