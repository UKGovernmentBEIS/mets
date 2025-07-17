package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AerDueDateServiceTest {

    @InjectMocks
    private AerDueDateService aerDueDateService;

    @Test
    void generateDueDate() {
    	Date expectedDate = DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 3, 31));
        // Invoke
        Date actualDate = aerDueDateService.generateDueDate();

        // Verify
        assertThat(actualDate).isEqualTo(expectedDate);
    }
}
