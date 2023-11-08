package uk.gov.pmrv.api.workflow.request.flow.installation.vir.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

@Service
public class VirDueDateService {

    public Date generateDueDate(Year year) {
        // For all VIRs the deadline is set at 30/06 of AER's year
        LocalDate deadline = LocalDate.of(year.getValue(), 6, 30);
        return DateUtils.convertLocalDateToDate(deadline);
    }
}
