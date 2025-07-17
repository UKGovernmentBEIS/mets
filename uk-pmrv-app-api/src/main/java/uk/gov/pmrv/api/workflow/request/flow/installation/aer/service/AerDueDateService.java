package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AerDueDateService {

    public Date generateDueDate() {
        // For all AERs generated automatically the deadline is set at 31/03
        return DateUtils.atEndOfDay(LocalDate.of(Year.now().getValue(), 3, 31));
    }
}
