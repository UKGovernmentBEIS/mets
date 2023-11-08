package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import java.time.LocalDate;
import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.service.DateService;

@Service
@RequiredArgsConstructor
public class CalculateAirExpirationRemindersService {

    private final DateService dateService;

    public LocalDate getExpirationDate() {

        final Year year = dateService.getYear();
        return LocalDate.of(year.getValue(), 6, 30);
    }
}
