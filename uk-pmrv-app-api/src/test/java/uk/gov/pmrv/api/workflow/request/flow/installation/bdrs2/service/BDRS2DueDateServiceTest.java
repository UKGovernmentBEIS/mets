package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

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
class BDRS2DueDateServiceTest {

    @InjectMocks
    private BDRS2DueDateService service;

    @Test
    void generateDueDate() {

        Date requireDate = DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 6, 30));
        Date dueDate = service.generateDueDate();

        assertThat(dueDate).isEqualTo(requireDate);
    }
}