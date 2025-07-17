package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AviationAerCorsiaAnnualOffsettingApplicationWaitForPeerReviewInitializerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingApplicationWaitForPeerReviewInitializer initializer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initializer = new AviationAerCorsiaAnnualOffsettingApplicationWaitForPeerReviewInitializer();
    }

    @Test
    void testGetRequestTaskTypes() {
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW));
    }

}
