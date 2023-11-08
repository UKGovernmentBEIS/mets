package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsApplicationWaitForAmendsInitializerTest {

    @InjectMocks
    private AviationAerUkEtsApplicationWaitForAmendsInitializer initializer;

    @Test
    void getRequestTaskPayloadType() {
        assertEquals(RequestTaskPayloadType.AVIATION_AER_UKETS_WAIT_FOR_AMENDS_PAYLOAD, initializer.getRequestTaskPayloadType());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsOnly(RequestTaskType.AVIATION_AER_UKETS_WAIT_FOR_AMENDS);
    }
}