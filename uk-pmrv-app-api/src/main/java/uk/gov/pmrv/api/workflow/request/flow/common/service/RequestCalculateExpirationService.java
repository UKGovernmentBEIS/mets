package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static java.time.temporal.ChronoUnit.MONTHS;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;

@Service
class RequestCalculateExpirationService {

    public Date calculateExpirationDate() {
        final LocalDate expiration = LocalDate.now().plus(2, MONTHS);
        return Date.from(expiration.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public Date calculateFirstReminderDate(Date expirationDate) {
        return DateUtils.addDays(expirationDate, -ExpirationReminderType.FIRST_REMINDER.getDaysToExpire());
    }
    
    public Date calculateSecondReminderDate(Date expirationDate) {
        return DateUtils.addDays(expirationDate, -ExpirationReminderType.SECOND_REMINDER.getDaysToExpire());
    }
    
}
