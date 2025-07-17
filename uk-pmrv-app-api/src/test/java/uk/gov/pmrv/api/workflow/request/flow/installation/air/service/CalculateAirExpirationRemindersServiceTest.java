package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Year;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.utils.DateService;

@ExtendWith(MockitoExtension.class)
class CalculateAirExpirationRemindersServiceTest {

    @InjectMocks
    private CalculateAirExpirationRemindersService service;

    @Mock
    private DateService dateService;

    @Test
    void getExpirationDate() {

        when(dateService.getYear()).thenReturn(Year.of(2022));

        final LocalDate expirationDate = service.getExpirationDate();

        Assertions.assertEquals(expirationDate, LocalDate.of(2022, 6, 30));
    }
}
