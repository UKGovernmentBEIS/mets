package uk.gov.pmrv.api.workflow.request.flow.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.utils.DateService;

@ExtendWith(MockitoExtension.class)
class RecalculateDueDateAfterRfiServiceTest {

    @InjectMocks
    private RecalculateDueDateAfterRfiService service;

    @Mock
    private DateService dateService;

    @Test
    void recalculateDueDate_days_passed_from_rfi_Start() {
    	LocalDateTime rfiStartDateTime = LocalDate.of(2020, Month.JANUARY, 1).atTime(11, 45);
        Date rfiStart = Date.from(rfiStartDateTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime expirationDateTime = LocalDate.of(2020, Month.FEBRUARY, 1).atTime(LocalTime.MAX);
        Date expiration = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());

        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2020, Month.JANUARY, 5, 12, 0));
        
        final LocalDateTime expectedDueDateTime = LocalDate.of(2020, Month.FEBRUARY, 1 + 4).atTime(LocalTime.MAX);
        final LocalDateTime actualDueDateTime = service.recalculateDueDate(rfiStart, expiration);

        assertThat(actualDueDateTime).isEqualToIgnoringNanos(expectedDueDateTime);

        verify(dateService, times(1)).getLocalDateTime();
    }

    @Test
    void recalculateDueDate_0_days_passed_from_rfi_Start() {
    	LocalDateTime rfiStartDateTime = LocalDate.of(2020, Month.JANUARY, 1).atTime(11, 45);
        Date rfiStart = Date.from(rfiStartDateTime.atZone(ZoneId.systemDefault()).toInstant());

        LocalDateTime expirationDateTime = LocalDate.of(2020, Month.FEBRUARY, 1).atTime(LocalTime.MAX);
        Date expiration = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());

        when(dateService.getLocalDateTime()).thenReturn(LocalDateTime.of(2020, Month.JANUARY, 1, 15, 45));
        
        final LocalDateTime expectedDueDateTime = LocalDate.of(2020, Month.FEBRUARY, 1 + 0).atTime(LocalTime.MAX);
        final LocalDateTime actualDueDateTime = service.recalculateDueDate(rfiStart, expiration);

        assertThat(actualDueDateTime).isEqualToIgnoringNanos(expectedDueDateTime);

        verify(dateService, times(1)).getLocalDateTime();
    }
}
