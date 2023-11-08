package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.CalculatePermitNotificationReviewExpirationDateService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CalculatePermitNotificationReviewExpirationDateServiceTest {

    @InjectMocks
    private CalculatePermitNotificationReviewExpirationDateService service;

    @Test
    void expirationDate() {
        final Date expected = Date.from(LocalDate.now()
                .plus(14, DAYS).
                atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        // Invoke
        Optional<Date> actual = service.expirationDate();

        // Verify
        assertThat(actual).isPresent().get().isEqualTo(expected);
    }

    @Test
    void getTypes() {
        assertThat(service.getTypes()).containsExactly(RequestType.PERMIT_NOTIFICATION);
    }
}
