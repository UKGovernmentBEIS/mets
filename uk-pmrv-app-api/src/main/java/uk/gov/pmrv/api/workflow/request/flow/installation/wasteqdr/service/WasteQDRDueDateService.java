package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class WasteQDRDueDateService {

    public Date generateDueDate() {
        // Waste QDR Deadline is set to 60 days from today
        return DateUtils.atEndOfDay(LocalDate.now().plusDays(60));
    }
}
