package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PermitVariationPreviewPermitDocumentBaseService {

    private final PermitQueryService permitQueryService;
    private final DateService dateService;
    private final PermitVariationRequestQueryService permitVariationRequestQueryService;
    private final PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService;

    protected FileDTO getFile(final DecisionNotification decisionNotification,
                              final Request request,
                              final Long accountId,
                              final Permit permit,
                              final PermitType permitType,
                              final LocalDate activationDate,
                              final SortedMap<String, BigDecimal> annualEmissionsTargets,
                              final Map<UUID, String> attachments,
                              final String logChanges) {
        
        final int newConsolidationNumber = permitQueryService.getPermitConsolidationNumberByAccountId(accountId) + 1;

        final List<PermitVariationRequestInfo> infoList =
            this.getPermitVariationRequestInfos(request, accountId, logChanges, newConsolidationNumber);
        
        return permitPreviewCreatePermitDocumentService.getFile(
            decisionNotification, 
            request, 
            accountId, 
            permit, 
            permitType, 
            activationDate,
            annualEmissionsTargets,
            attachments,
            newConsolidationNumber, 
            infoList);
    }

    private List<PermitVariationRequestInfo> getPermitVariationRequestInfos(final Request request, 
                                                                            final Long accountId,
                                                                            final String logChanges,
                                                                            final int newConsolidationNumber) {
        
        final List<PermitVariationRequestInfo> variationHistoricalRequests = 
            permitVariationRequestQueryService.findPermitVariationRequests(accountId);

        final PermitVariationRequestMetadata requestMetadata = (PermitVariationRequestMetadata) request.getMetadata();
        final PermitVariationRequestInfo variationCurrentRequest = PermitVariationRequestInfo.builder()
            .id(request.getId())
            .submissionDate(request.getSubmissionDate())
            .endDate(dateService.getLocalDateTime())
            .metadata(PermitVariationRequestMetadata.builder()
                .type(RequestMetadataType.PERMIT_VARIATION)
                .rfiResponseDates(requestMetadata.getRfiResponseDates())
                .logChanges(logChanges)
                .permitConsolidationNumber(newConsolidationNumber)
                .initiatorRoleType(requestMetadata.getInitiatorRoleType())
                .build())
            .build();

        return Stream.concat(variationHistoricalRequests.stream(), Stream.of(variationCurrentRequest)).toList();
    }
}
