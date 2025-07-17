package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitIssuancePreviewPermitDocumentServiceTest {

    @InjectMocks
    private PermitIssuancePreviewPermitDocumentService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService;

    @Test
    void create() {

        final long accountId = 2L;
        final long taskId = 2L;
        final Request request = Request.builder().accountId(accountId).build();
        final Permit permit = Permit.builder().build();
        final PermitType permitType = PermitType.GHGE;
        final LocalDate activationDate = LocalDate.of(2022, 1, 1);
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
        final TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>(Map.of("2022", new BigDecimal(12345)));
        final RequestTask requestTask = RequestTask.builder()
            .id(taskId)
            .request(request)
            .payload(PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permit(permit)
                .permitType(permitType)
                .determination(PermitIssuanceGrantDetermination.builder().activationDate(activationDate).annualEmissionsTargets(
                    annualEmissionsTargets
                ).build())
                .permitAttachments(attachments)
                .build())
            .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        
        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        
        service.create(taskId, decisionNotification);

        verify(requestTaskService, times(1)).findTaskById(taskId);
        verify(permitPreviewCreatePermitDocumentService, times(1)).getFile(
            decisionNotification, request, accountId, permit, permitType, activationDate, annualEmissionsTargets, attachments, 1, Collections.emptyList()
        );
    }
}
