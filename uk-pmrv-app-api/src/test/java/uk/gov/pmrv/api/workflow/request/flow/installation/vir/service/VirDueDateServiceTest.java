package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

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
class VirDueDateServiceTest {

    @InjectMocks
    private VirDueDateService virDueDateService;

    @Test
    void generateDueDate() {
        final Year year = Year.of(2022);
        Date expectedDate = DateUtils.atEndOfDay(LocalDate.of(year.getValue(), 6, 30));
        // Invoke
        Date actualDate = virDueDateService.generateDueDate(year);

        // Verify
        assertThat(actualDate).isEqualTo(expectedDate);
    }
}
