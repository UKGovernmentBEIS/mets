package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.mapper.AerAviationCorsiaAnnualOffsettingMapper;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AviationAerCorsiaAnnualOffsetingApplicationPeerReviewInitializerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsetingApplicationPeerReviewInitializer initializer;

    @Mock
    private Request request;


    private AerAviationCorsiaAnnualOffsettingMapper mapper = Mappers.getMapper(AerAviationCorsiaAnnualOffsettingMapper.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetRequestTaskTypes() {
        Set<RequestTaskType> result = initializer.getRequestTaskTypes();
        assertEquals(1, result.size());
        assertTrue(result.contains(RequestTaskType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW));
    }

    @Test
    void testGetRequestTaskPayloadType() {
        RequestTaskPayloadType result = invokePrivateGetRequestTaskPayloadType();
        assertEquals(RequestTaskPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_PEER_REVIEW_PAYLOAD, result);
    }

    private RequestTaskPayloadType invokePrivateGetRequestTaskPayloadType() {
        try {
            var method = AviationAerCorsiaAnnualOffsetingApplicationPeerReviewInitializer.class.getDeclaredMethod("getRequestTaskPayloadType");
            method.setAccessible(true);
            return (RequestTaskPayloadType) method.invoke(initializer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
