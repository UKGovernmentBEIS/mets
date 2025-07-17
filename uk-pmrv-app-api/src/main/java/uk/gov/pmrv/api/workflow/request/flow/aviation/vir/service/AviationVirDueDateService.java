package uk.gov.pmrv.api.workflow.request.flow.aviation.vir.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@Service
public class AviationVirDueDateService {

    public Date generateDueDate(Year year) {
        // For all VIRs the deadline is set at 30/06 of AER's year
        return DateUtils.atEndOfDay(LocalDate.of(year.getValue(), 6, 30));
    }
}
