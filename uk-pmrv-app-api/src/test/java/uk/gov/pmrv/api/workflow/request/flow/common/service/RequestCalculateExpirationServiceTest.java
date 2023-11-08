package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.apache.commons.lang3.time.DateUtils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.MONTHS;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestCalculateExpirationServiceTest {

    @InjectMocks
    private RequestCalculateExpirationService requestCalculateExpirationService;

    @Test
    void calculateExpirationDate() {
        final Date expected = Date.from(LocalDate.now()
                .plus(2, MONTHS)
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        // Invoke
        Date actual = requestCalculateExpirationService.calculateExpirationDate();

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateFirstReminderDate() {
        final Date expirationDate = Date.from(LocalDate.now()
                .plus(2, MONTHS)
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        final Date expected = DateUtils.addDays(expirationDate, -ExpirationReminderType.FIRST_REMINDER.getDaysToExpire());

        // Invoke
        Date actual = requestCalculateExpirationService.calculateFirstReminderDate(expirationDate);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateSecondReminderDate() {
        final Date expirationDate = Date.from(LocalDate.now()
                .plus(2, MONTHS)
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant());
        final Date expected = DateUtils.addDays(expirationDate, -ExpirationReminderType.SECOND_REMINDER.getDaysToExpire());

        // Invoke
        Date actual = requestCalculateExpirationService.calculateSecondReminderDate(expirationDate);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
