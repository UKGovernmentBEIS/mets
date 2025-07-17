package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CalculatePermitNotificationReviewExpirationDateServiceTest {

    @InjectMocks
    private CalculatePermitNotificationReviewExpirationDateService service;

    @Test
    void expirationDate() {
        final Date expected = DateUtils.atEndOfDay(LocalDate.now()
                .plusDays(14));

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
