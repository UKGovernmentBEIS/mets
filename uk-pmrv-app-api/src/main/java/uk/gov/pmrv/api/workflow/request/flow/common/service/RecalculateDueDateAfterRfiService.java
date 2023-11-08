package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.service.DateService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@RequiredArgsConstructor
public class RecalculateDueDateAfterRfiService {
    
    private final DateService dateService;

    public LocalDateTime recalculateDueDate(final Date rfiStart, final Date expirationDate) {
        final LocalDateTime rfiStartDt = LocalDateTime.ofInstant(rfiStart.toInstant(), ZoneId.systemDefault());
        final LocalDateTime rfiEndDt = dateService.getLocalDateTime();
        final long rfiDuration = DAYS.between(rfiStartDt.toLocalDate(), rfiEndDt.toLocalDate());
        final LocalDateTime expirationDt = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
        final LocalDateTime dueDateDt = expirationDt
            .plus(rfiDuration, DAYS)
            .toLocalDate()
            .atTime(LocalTime.MIN);
        
        return expirationDt.isAfter(dueDateDt) ? expirationDt : dueDateDt;
    }
    
}
