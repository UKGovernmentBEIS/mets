package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.utils.DateUtils;


import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class BDRDueDateService {


    public Date generateDueDate() {
        // For all BDRs generated automatically the deadline is set at 30/06/2025.
        return DateUtils.atEndOfDay(LocalDate.of(2025, 6, 30));
    }
}
