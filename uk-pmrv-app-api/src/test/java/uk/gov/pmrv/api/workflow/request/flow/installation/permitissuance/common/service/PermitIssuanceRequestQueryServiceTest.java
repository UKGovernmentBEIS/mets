package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermitIssuanceRequestQueryServiceTest {

    @InjectMocks
    private PermitIssuanceRequestQueryService cut;

    @Mock
    private RequestRepository requestRepository;

    @Test
    void findPermitIssuanceMetadata() {
        Long accountId = 1L;
        PermitIssuanceRequestMetadata permitIssuanceRequestMetadata = PermitIssuanceRequestMetadata.builder()
                .type(RequestMetadataType.PERMIT_ISSUANCE)
                .rfiResponseDates(List.of(LocalDateTime.now()))
                .build();
        List<Request> requests = List.of(
                Request.builder().id("request1").status(RequestStatus.APPROVED)
                        .accountId(accountId)
                        .type(RequestType.PERMIT_ISSUANCE)
                        .metadata(permitIssuanceRequestMetadata)
                        .build()
        );

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED)).thenReturn(requests);

        PermitIssuanceRequestMetadata result = cut.findPermitIssuanceMetadataByAccountId(accountId);
        assertThat(result).isEqualTo(permitIssuanceRequestMetadata);
        verify(requestRepository, times(1)).findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED);
    }

    @Test
    void findPermitIssuanceMetadata_multiple_found() {
        Long accountId = 1L;
        List<Request> requests = List.of(
                Request.builder().id("request1").status(RequestStatus.APPROVED).accountId(accountId).type(RequestType.PERMIT_ISSUANCE).build(),
                Request.builder().id("request2").status(RequestStatus.APPROVED).accountId(accountId).type(RequestType.PERMIT_ISSUANCE).build()
        );

        PermitIssuanceRequestMetadata permitIssuanceRequestMetadata = PermitIssuanceRequestMetadata.builder()
                .type(RequestMetadataType.PERMIT_ISSUANCE)
                .build();

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED)).thenReturn(requests);

        PermitIssuanceRequestMetadata result = cut.findPermitIssuanceMetadataByAccountId(accountId);
        assertThat(result).isEqualTo(permitIssuanceRequestMetadata);
        verify(requestRepository, times(1)).findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED);
    }

    @Test
    void findPermitIssuanceMetadata_not_found() {
        Long accountId = 1L;
        List<Request> requests = List.of();

        when(requestRepository.findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED)).thenReturn(requests);

        PermitIssuanceRequestMetadata permitIssuanceRequestMetadata = PermitIssuanceRequestMetadata.builder()
                .type(RequestMetadataType.PERMIT_ISSUANCE)
                .build();
        PermitIssuanceRequestMetadata result = cut.findPermitIssuanceMetadataByAccountId(accountId);
        assertThat(result).isEqualTo(permitIssuanceRequestMetadata);
        verify(requestRepository, times(1)).findByAccountIdAndTypeAndStatus(accountId,
                RequestType.PERMIT_ISSUANCE, RequestStatus.APPROVED);
    }
}
