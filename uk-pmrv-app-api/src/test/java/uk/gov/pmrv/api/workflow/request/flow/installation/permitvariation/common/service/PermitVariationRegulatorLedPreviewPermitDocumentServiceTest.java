package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit.PermitPreviewCreatePermitDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationRegulatorLedGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitVariationRegulatorLedPreviewPermitDocumentServiceTest {

    @InjectMocks
    private PermitVariationRegulatorLedPreviewPermitDocumentService service;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitQueryService permitQueryService;
    
    @Mock
    private DateService dateService;
    
    @Mock
    private PermitVariationRequestQueryService permitVariationRequestQueryService;

    @Mock
    private PermitPreviewCreatePermitDocumentService permitPreviewCreatePermitDocumentService;
    
    @Test
    void createPermitForVariationRegulatorLed() {

        final Long taskId = 100L;
        final Long accountId = 200L;
        final String signatory = "signatory";
        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory(signatory).build();
        final LocalDate activationDate = LocalDate.of(2022, 1, 1);
        final String logChanges = "the changes";
        final LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 4, 6, 7);
        final List<LocalDateTime> rfiResponseDates = List.of(LocalDateTime.of(2023, 1, 1, 5, 7));
        final Request request = Request.builder()
            .accountId(accountId)
            .submissionDate(submissionDate)
            .metadata(PermitVariationRequestMetadata.builder()
                .initiatorRoleType(RoleTypeConstants.OPERATOR)
                .rfiResponseDates(rfiResponseDates)
                .build())
            .build();
        final BigDecimal estimatedEmissions = new BigDecimal(2000);
        final Permit permit = Permit.builder()
            .estimatedAnnualEmissions(
                EstimatedAnnualEmissions.builder().quantity(estimatedEmissions).build()).build();
        final PermitType permitType = PermitType.GHGE;
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
        final TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>(Map.of("2022", new BigDecimal(12345)));
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT)
            .request(request)
            .payload(PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload.builder()
                .permit(permit)
                .permitType(permitType)
                .determination(
                    PermitVariationRegulatorLedGrantDetermination.builder()
                        .activationDate(activationDate)
                        .annualEmissionsTargets(annualEmissionsTargets)
                        .logChanges(logChanges)
                        .build()
                )
                .permitAttachments(attachments)
                .build())
            .build();
        final LocalDateTime now = LocalDateTime.of(2023, 1, 2, 3, 4);
        final int consolidationNumber = 2;
        final int newConsolidationNumber = consolidationNumber + 1;
        final List<PermitVariationRequestInfo> variationRequestInfos = List.of(PermitVariationRequestInfo.builder()
            .submissionDate(submissionDate)
            .endDate(now)
            .build());
        final PermitVariationRequestInfo variationRequestInfo =
            PermitVariationRequestInfo.builder().endDate(now).submissionDate(submissionDate).metadata(
                PermitVariationRequestMetadata.builder()
                    .type(RequestMetadataType.PERMIT_VARIATION)
                    .rfiResponseDates(rfiResponseDates)
                    .logChanges(logChanges)
                    .initiatorRoleType(RoleTypeConstants.OPERATOR)
                    .permitConsolidationNumber(newConsolidationNumber)
                    .build()
            ).build();
        final List<PermitVariationRequestInfo> allVariations = new ArrayList<>(variationRequestInfos);
        allVariations.add(variationRequestInfo);
        final String fileName = "fileName";
        final FileDTO fileDTO = FileDTO.builder().fileName(fileName).build();

        when(requestTaskService.findTaskById(taskId)).thenReturn(requestTask);
        when(permitQueryService.getPermitConsolidationNumberByAccountId(accountId)).thenReturn(consolidationNumber);
        when(permitVariationRequestQueryService.findPermitVariationRequests(accountId)).thenReturn(
            variationRequestInfos
        );
        when(dateService.getLocalDateTime()).thenReturn(now);
        when(permitPreviewCreatePermitDocumentService.getFile(decisionNotification, request, accountId, permit,
            permitType, activationDate, annualEmissionsTargets, attachments, newConsolidationNumber, allVariations)).thenReturn(fileDTO);

        final FileDTO result = service.create(taskId, decisionNotification);

        assertEquals(result.getFileName(), fileName);
    }
}
