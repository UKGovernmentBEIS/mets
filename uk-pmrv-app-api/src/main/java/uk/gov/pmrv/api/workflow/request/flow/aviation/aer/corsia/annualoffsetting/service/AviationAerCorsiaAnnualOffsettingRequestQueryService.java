package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class AviationAerCorsiaAnnualOffsettingRequestQueryService {

    private final RequestRepository requestRepository;

    public Optional<Request> findLatestCorsiaAnnualOffsettingRequestForYear(Long accountId, Year metadataYear) {

        Optional<Request> inProgressAnnualOffsetting = requestRepository
                .findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                        accountId,
                        List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                        metadataYear.getValue(),
                        List.of(RequestStatus.IN_PROGRESS.name()))
                .stream()
                .findFirst();

        if (inProgressAnnualOffsetting.isPresent()) {
            return inProgressAnnualOffsetting;
        }

        return requestRepository
                .findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                        accountId,
                        List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                        metadataYear.getValue(),
                        List.of(RequestStatus.COMPLETED.name()))
                .stream()
                .findFirst();
    }
}
