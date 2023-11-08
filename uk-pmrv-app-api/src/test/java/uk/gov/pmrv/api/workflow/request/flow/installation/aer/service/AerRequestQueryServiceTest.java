package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import uk.gov.pmrv.api.common.service.DateService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerRequestQueryServiceTest {

    @InjectMocks
    private AerRequestQueryService aerRequestQueryService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private DateService dateService;

    @Test
    void getApprovedPermitNotificationIdsByAccount() {
        Long accountId = 1L;
        Request request1 = Request.builder()
            .id("request1")
            .status(RequestStatus.APPROVED)
            .accountId(accountId)
            .type(RequestType.PERMIT_NOTIFICATION)
            .build();
        Request request2 = Request.builder()
            .id("request2")
            .status(RequestStatus.APPROVED)
            .accountId(accountId)
            .type(RequestType.PERMIT_NOTIFICATION)
            .build();
        List<Request> requests = List.of(request1, request2);

        when(requestRepository
            .findByAccountIdAndTypeAndStatus(accountId, RequestType.PERMIT_NOTIFICATION,
                RequestStatus.APPROVED, Sort.by(Sort.Direction.DESC, "endDate"))
        ).thenReturn(requests);

        List<String> result = aerRequestQueryService.getApprovedPermitNotificationIdsByAccount(accountId);
        assertThat(result).containsExactlyInAnyOrder(request1.getId(), request2.getId());

        verify(requestRepository, times(1))
            .findByAccountIdAndTypeAndStatus(accountId, RequestType.PERMIT_NOTIFICATION,
                RequestStatus.APPROVED, Sort.by(Sort.Direction.DESC, "endDate")
            );
    }

    @Test
    void findApprovedPermitIssuanceRequest() {
        Long accountId = 1L;
        PermitIssuanceRequestMetadata permitIssuanceRequestMetadata = PermitIssuanceRequestMetadata.builder()
            .type(RequestMetadataType.PERMIT_ISSUANCE)
            .rfiResponseDates(List.of(LocalDateTime.now()))
            .build();
        LocalDateTime currentDate = LocalDateTime.now();
        List<Request> requests = List.of(
            Request.builder().id("request1").status(RequestStatus.APPROVED)
                .accountId(accountId)
                .type(RequestType.PERMIT_ISSUANCE)
                .endDate(currentDate)
                .metadata(permitIssuanceRequestMetadata)
                .build()
        );

        when(requestRepository.findByAccountIdAndTypeInAndStatus(accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B), RequestStatus.APPROVED)).thenReturn(requests);

        Optional<LocalDateTime> result = aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId);
        assertTrue(result.isPresent());
        assertThat(result.get()).isEqualTo(currentDate);
        verify(requestRepository, times(1)).findByAccountIdAndTypeInAndStatus(
            accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B),
            RequestStatus.APPROVED
        );
    }

    @Test
    void findApprovedPermitIssuanceRequest_BusinessException_When_No_Approved_Permit_Issuance() {
        Long accountId = 1L;

        when(requestRepository.findByAccountIdAndTypeInAndStatus(accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B), RequestStatus.APPROVED)).thenReturn(List.of());

        Optional<LocalDateTime> endDateOfApprovedPermitIssuanceOrTransferBByAccountId = aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId);
        assert(endDateOfApprovedPermitIssuanceOrTransferBByAccountId.isEmpty());
        verify(requestRepository, times(1)).findByAccountIdAndTypeInAndStatus(
            accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B),
            RequestStatus.APPROVED
        );
    }

    @Test
    void findApprovedPermitTransferBRequest() {
        Long accountId = 1L;
        List<Request> requests = List.of(
            Request.builder().id("request1").status(RequestStatus.APPROVED)
                .accountId(accountId)
                .type(RequestType.PERMIT_TRANSFER_B)
                .build()
        );
        final LocalDateTime now = LocalDateTime.of(2022, 1, 2, 3, 4, 5);

        when(requestRepository.findByAccountIdAndTypeInAndStatus(accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B), RequestStatus.APPROVED)).thenReturn(requests);
        when(dateService.getLocalDateTime()).thenReturn(now);

        Optional<LocalDateTime> result = aerRequestQueryService.findEndDateOfApprovedPermitIssuanceOrTransferBByAccountId(accountId);
        assertTrue(result.isPresent());
        assertThat(result.get()).isEqualTo(now);
        verify(requestRepository, times(1)).findByAccountIdAndTypeInAndStatus(
            accountId,
            Set.of(RequestType.PERMIT_ISSUANCE, RequestType.PERMIT_TRANSFER_B),
            RequestStatus.APPROVED
        );
    }
}