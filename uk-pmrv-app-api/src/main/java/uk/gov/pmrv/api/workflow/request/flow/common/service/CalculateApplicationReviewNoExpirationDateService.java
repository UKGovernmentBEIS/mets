package uk.gov.pmrv.api.workflow.request.flow.common.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CalculateApplicationReviewNoExpirationDateService implements CalculateApplicationReviewExpirationDateService {

    @Override
    public Optional<Date> expirationDate() {
        return Optional.empty();
    }

    @Override
    public Set<RequestType> getTypes() {
        return Set.of(RequestType.VIR, RequestType.AVIATION_VIR, RequestType.AIR);
    }
}
