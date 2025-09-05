package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class HSETIExpirationDateService {

    public Date calculateExpirationDate() {
        LocalDate localDate = LocalDate.now().plusMonths(2);
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
