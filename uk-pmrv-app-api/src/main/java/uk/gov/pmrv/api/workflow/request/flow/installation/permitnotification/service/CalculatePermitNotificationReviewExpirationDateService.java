package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CalculateApplicationReviewExpirationDateService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class CalculatePermitNotificationReviewExpirationDateService implements CalculateApplicationReviewExpirationDateService {

    @Override
    public Optional<Date> expirationDate() {
        final LocalDate expirationLocal = LocalDate.now().plus(14, DAYS);
        final Date expiration = Date.from(expirationLocal.atTime(LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());

        return Optional.of(expiration);
    }

    @Override
    public Set<RequestType> getTypes() {
        return Set.of(RequestType.PERMIT_NOTIFICATION);
    }
}
