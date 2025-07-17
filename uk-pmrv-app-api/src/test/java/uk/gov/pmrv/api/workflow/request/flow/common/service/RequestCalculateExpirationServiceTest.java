package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestCalculateExpirationServiceTest {

    @InjectMocks
    private RequestCalculateExpirationService requestCalculateExpirationService;

    @Test
    void calculateExpirationDate() {
        final Date expected = DateUtils.atEndOfDay(LocalDate.now().plusMonths(2));

        // Invoke
        Date actual = requestCalculateExpirationService.calculateExpirationDate();

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateFirstReminderDate() {
        final Date expirationDate = DateUtils.atEndOfDay(LocalDate.now()
                .plusMonths(2));
        final Date expected = org.apache.commons.lang3.time.DateUtils.addDays(expirationDate, -ExpirationReminderType.FIRST_REMINDER.getDaysToExpire());

        // Invoke
        Date actual = requestCalculateExpirationService.calculateFirstReminderDate(expirationDate);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateSecondReminderDate() {
        final Date expirationDate = DateUtils.atEndOfDay(LocalDate.now()
                .plusMonths(2));
        final Date expected = org.apache.commons.lang3.time.DateUtils.addDays(expirationDate, -ExpirationReminderType.SECOND_REMINDER.getDaysToExpire());

        // Invoke
        Date actual = requestCalculateExpirationService.calculateSecondReminderDate(expirationDate);

        // Verify
        assertThat(actual).isEqualTo(expected);
    }
}
