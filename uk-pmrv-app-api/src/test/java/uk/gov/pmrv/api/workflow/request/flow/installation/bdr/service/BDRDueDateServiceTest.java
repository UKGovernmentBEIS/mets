package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BDRDueDateServiceTest {

    @InjectMocks
    private BDRDueDateService service;

    @Test
    void generateDueDate() {

        Date requireDate = DateUtils.atEndOfDay(LocalDate.of(2025, 6, 30));
        Date dueDate = service.generateDueDate();

        assertThat(dueDate).isEqualTo(requireDate);
    }
}
