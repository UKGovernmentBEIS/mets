package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class ALRDueDateService {

    public Date generateDueDate() {
        // For all ALRs generated automatically the deadline is set at 31/03
        return DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 3, 31));
    }
}
