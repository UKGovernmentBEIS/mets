package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service.PermitIssuanceCreatePermitDocumentService;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceCreatePermitDocumentServiceTest {

    @InjectMocks
    private PermitIssuanceCreatePermitDocumentService cut;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private PermitCreateDocumentService permitCreateDocumentService;

    @Test
    void create() {

        final String requestId = "1";
        final long accountId = 5L;
        final String signatory = "signatory";
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .decisionNotification(DecisionNotification.builder()
                .signatory(signatory)
                .build())
            .build();
        final List<LocalDateTime> rfiResponseDates = List.of(
            LocalDateTime.of(2022, 1, 1, 1, 1),
            LocalDateTime.of(2022, 2, 2, 2, 2)
        );
        final PermitIssuanceRequestMetadata metadata = PermitIssuanceRequestMetadata.builder()
            .rfiResponseDates(rfiResponseDates)
            .build();
        final Request request =
            Request.builder().accountId(accountId).payload(requestPayload).metadata(metadata).build();
        final PermitEntityDto permitEntityDto = PermitEntityDto.builder().id("permitId").build();
        final FileInfoDTO permitDocument = FileInfoDTO.builder().uuid("uuid").build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(permitQueryService.getPermitByAccountId(accountId)).thenReturn(permitEntityDto);
        when(permitCreateDocumentService.generateDocumentAsync(request, signatory, permitEntityDto, metadata, Collections.emptyList()))
            .thenReturn(CompletableFuture.completedFuture(permitDocument));

        cut.create(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(permitQueryService, times(1)).getPermitByAccountId(accountId);
        verify(permitCreateDocumentService, times(1)).generateDocumentAsync(request, signatory, permitEntityDto, metadata, Collections.emptyList());
    }
}
