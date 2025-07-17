package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class EmpIssuanceUkEtsPeerReviewPreviewEmpDocumentServiceTest {

    @InjectMocks
    private EmpIssuanceUkEtsPeerReviewPreviewEmpDocumentService service;

    @Test
    void getType() {
        assertEquals(List.of(RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW), service.getTypes());
    }
}
