package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class RecalculateDueDateAfterRfiService {
    
    private final DateService dateService;

	public LocalDateTime recalculateDueDate(final Date rfiStart, final Date expirationDate) {
		final LocalDateTime rfiStartDt = LocalDateTime.ofInstant(rfiStart.toInstant(), ZoneId.systemDefault());
		final LocalDateTime rfiEndDt = dateService.getLocalDateTime();
		final long rfiDurationInDays = ChronoUnit.DAYS.between(rfiStartDt, rfiEndDt);
		final LocalDateTime expirationDt = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());
		return expirationDt.plusDays(rfiDurationInDays);
	}
    
}
