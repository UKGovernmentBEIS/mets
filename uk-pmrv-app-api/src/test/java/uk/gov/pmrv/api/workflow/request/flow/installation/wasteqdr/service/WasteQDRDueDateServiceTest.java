package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class WasteQDRDueDateServiceTest {

    @InjectMocks
    private WasteQDRDueDateService service;

    @Test
    void generateDueDate() {
        Date requireDate = DateUtils.atEndOfDay(LocalDate.now().plusDays(60));
        Date dueDate = service.generateDueDate();

        assertThat(dueDate).isEqualTo(requireDate);
    }
}
