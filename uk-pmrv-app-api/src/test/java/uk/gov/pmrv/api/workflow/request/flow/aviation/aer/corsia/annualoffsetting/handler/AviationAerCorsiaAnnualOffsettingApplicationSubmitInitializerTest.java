package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

import java.time.Year;
import java.util.HashMap;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaAnnualOffsettingApplicationSubmitInitializerTest {
    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingApplicationSubmitInitializer initializer;

    @Test
    void testInitializePayload() {
        // Arrange
        int schemeYear = 2024;
        HashMap<String, Boolean> sectionsCompleted = new HashMap<>();
        sectionsCompleted.put("Test", true);
        DecisionNotification decisionNotification = DecisionNotification.builder().signatory("signatory").build();

        AviationAerCorsiaAnnualOffsettingRequestMetadata metadata =
                AviationAerCorsiaAnnualOffsettingRequestMetadata.builder().year(Year.of(schemeYear)).build();

        AviationAerCorsiaAnnualOffsettingRequestPayload payload = AviationAerCorsiaAnnualOffsettingRequestPayload.builder()
                .aviationAerCorsiaAnnualOffsettingSectionsCompleted(sectionsCompleted)
                .decisionNotification(decisionNotification)
                .build();

        Request request = Request.builder().payload(payload).metadata(metadata).build();

        // Act
        AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload result =
                (AviationAerCorsiaAnnualOffsettingApplicationSubmitRequestTaskPayload) initializer.initializePayload(request);

        // Assert
        assertNotNull(result);
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertEquals(Year.of(schemeYear), result.getAviationAerCorsiaAnnualOffsetting().getSchemeYear());
        assertEquals(sectionsCompleted, result.getAviationAerCorsiaAnnualOffsettingSectionsCompleted());
        assertEquals(decisionNotification, result.getDecisionNotification());
    }

    @Test
    void testGetRequestTaskTypes() {
        // Act
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT));
    }
}
