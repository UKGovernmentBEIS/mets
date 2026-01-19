package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

@Service
public class BDRS2DueDateService {
    public Date generateDueDate() {
        // For all BDRS2s generated automatically, the deadline is set at 30/06
        return DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 6, 30));
    }
}
