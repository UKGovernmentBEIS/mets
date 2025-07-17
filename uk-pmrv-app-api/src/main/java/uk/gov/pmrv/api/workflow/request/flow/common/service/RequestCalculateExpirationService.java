package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.time.LocalDate;
import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;

@Service
class RequestCalculateExpirationService {

    public Date calculateExpirationDate() {
        final LocalDate expiration = LocalDate.now().plusMonths(2);
        return uk.gov.pmrv.api.workflow.utils.DateUtils.atEndOfDay(expiration);
    }
    
    public Date calculateFirstReminderDate(Date expirationDate) {
        return DateUtils.addDays(expirationDate, -ExpirationReminderType.FIRST_REMINDER.getDaysToExpire());
    }
    
    public Date calculateSecondReminderDate(Date expirationDate) {
        return DateUtils.addDays(expirationDate, -ExpirationReminderType.SECOND_REMINDER.getDaysToExpire());
    }
    
}
