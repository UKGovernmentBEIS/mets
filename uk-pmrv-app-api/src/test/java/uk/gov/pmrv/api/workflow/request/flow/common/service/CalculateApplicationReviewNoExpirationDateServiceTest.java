package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CalculateApplicationReviewNoExpirationDateServiceTest {

    @InjectMocks
    private CalculateApplicationReviewNoExpirationDateService service;

    @Test
    void expirationDate() {
        // Invoke
        Optional<Date> actual = service.expirationDate();

        // Verify
        assertThat(actual).isNotPresent();
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).containsExactlyInAnyOrder(RequestType.VIR, RequestType.AVIATION_VIR, RequestType.AIR);
    }
}
