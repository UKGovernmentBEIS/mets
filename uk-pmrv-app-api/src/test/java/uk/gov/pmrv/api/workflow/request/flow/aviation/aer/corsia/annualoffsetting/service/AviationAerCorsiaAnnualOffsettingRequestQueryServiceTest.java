package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.service;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsiaAnnualOffsettingRequestQueryServiceTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingRequestQueryService queryService;

    @Mock
    private RequestRepository requestRepository;


    @Test
    void findLatestCorsiaAnnualOffsettingRequestForYear_whenInProgressAnnualOffsettingExists_returnIt(){

        Long accountId = 1L;
        Year year = Year.of(2023);
        LocalDateTime endDate = LocalDateTime.now();

        Request request = Request.builder()
                                .id("id")
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .endDate(endDate)
                                .status(RequestStatus.IN_PROGRESS)
                        .build();


        when(requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                year.getValue(),
                List.of(RequestStatus.IN_PROGRESS.name())))
                .thenReturn(List.of(request));

        final Optional<Request> actual = queryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(request);
    }

    @Test
    void findLatestCorsiaAnnualOffsettingRequestForYear_whenInProgressAnnualOffsettingDoesntExistAndCompletedDoes_returnTheLatestCompleted(){

        Long accountId = 1L;
        Year year = Year.of(2023);
        LocalDateTime endDate = LocalDateTime.now();

        Request request = Request.builder()
                                .id("id")
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .endDate(endDate.plusDays(10))
                                .status(RequestStatus.COMPLETED)
                        .build();


        Request request1 = Request.builder()
                                .id("id")
                                .type(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING)
                                .endDate(endDate)
                                .status(RequestStatus.COMPLETED)
                        .build();

        when(requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                year.getValue(),
                List.of(RequestStatus.IN_PROGRESS.name())))
                .thenReturn(List.of());

        when(requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                year.getValue(),
                List.of(RequestStatus.COMPLETED.name())))
                .thenReturn(List.of(request,request1));

        final Optional<Request> actual = queryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year);

        assertThat(actual).isPresent();
        assertThat(actual.get()).isEqualTo(request);
    }

    @Test
    void findLatestCorsiaAnnualOffsettingRequestForYear_requestDoesntExist_returnEmptyOptional(){
        Long accountId = 1L;
        Year year = Year.of(2023);


        when(requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                year.getValue(),
                List.of(RequestStatus.IN_PROGRESS.name())))
                .thenReturn(List.of());

        when(requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING.name()),
                year.getValue(),
                List.of(RequestStatus.IN_PROGRESS.name())))
                .thenReturn(List.of());

        final Optional<Request> actual = queryService.findLatestCorsiaAnnualOffsettingRequestForYear(accountId,year);
        assertThat(actual).isNotPresent();
    }
}
