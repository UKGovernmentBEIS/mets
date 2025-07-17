package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.CalculateApplicationReviewExpirationDateService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
public class CalculatePermitNotificationReviewExpirationDateService implements CalculateApplicationReviewExpirationDateService {

    @Override
    public Optional<Date> expirationDate() {
        final LocalDate expirationDate = LocalDate.now().plusDays(14);
        return Optional.of(DateUtils.atEndOfDay(expirationDate));
    }

    @Override
    public Set<RequestType> getTypes() {
        return Set.of(RequestType.PERMIT_NOTIFICATION);
    }
}
