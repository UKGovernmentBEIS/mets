package uk.gov.pmrv.api.common.service;

import java.time.LocalDateTime;
import java.time.Year;
import org.springframework.stereotype.Service;

@Service
public class DateService {

    public LocalDateTime getLocalDateTime() {
        return LocalDateTime.now();
    }

    public Year getYear() {
        return Year.now();
    }
}
