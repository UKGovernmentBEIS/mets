package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class AviationVirDueDateServiceTest {

    @InjectMocks
    private AviationVirDueDateService virDueDateService;

    @Test
    void generateDueDate() {
        final Year year = Year.of(2022);
        final Date expectedDate = DateUtils.atEndOfDay(LocalDate.of(year.getValue(), 6, 30));
        
        final Date actualDate = virDueDateService.generateDueDate(year);

        assertThat(actualDate).isEqualTo(expectedDate);
    }
}
