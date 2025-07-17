package uk.gov.pmrv.api.workflow.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@UtilityClass
public class DateUtils {

    public Long getTaskExpirationDaysRemaining(LocalDate taskPauseDate, LocalDate taskDueDate) {
    	if(ObjectUtils.isEmpty(taskDueDate)) {
    		return null;
    	}
    	
		return DAYS.between(ObjectUtils.isEmpty(taskPauseDate) ? LocalDate.now() : taskPauseDate, taskDueDate);
    }

    public Date atEndOfDay(LocalDate date) {
        return Date.from(date
            .atTime(LocalTime.MAX)
            .atZone(ZoneId.systemDefault())
            .toInstant());
    }
    
}
