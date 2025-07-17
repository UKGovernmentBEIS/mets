package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.account.installation.transform.InstallationCategoryMapper;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermitPreviewCreatePermitDocumentService {

    private final DocumentTemplatePermitParamsProvider permitParamsProvider;
    private final PermitQueryService permitQueryService;
    private final PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;
    private final PermitCreateDocumentService permitCreateDocumentService;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    private final PermitIdentifierGenerator generator;

    public FileDTO getFile(final DecisionNotification decisionNotification,
                           final Request request,
                           final Long accountId,
                           final Permit permit,
                           final PermitType permitType,
                           final LocalDate activationDate,
                           final SortedMap<String, BigDecimal> annualEmissionsTargets,
                           final Map<UUID, String> attachments,
                           final int consolidationNumber,
                           final List<PermitVariationRequestInfo> infoList) {

        final String signatory = decisionNotification.getSignatory();

        final PermitEntityDto permitEntityDto =
            this.createPermitEntityDto(accountId, permit, permitType, activationDate, annualEmissionsTargets, attachments, consolidationNumber);

        final PermitIssuanceRequestMetadata issuanceMetadata = permitIssuanceRequestQueryService
            .findPermitIssuanceMetadataByAccountId(accountId);

        final TemplateParams permitParams = this.constructTemplateParams(request, signatory, permitEntityDto, issuanceMetadata, infoList);

        this.updateAccountParams(permit, permitType, permitParams);

        return permitCreateDocumentService.generateDocumentWithParams(permitEntityDto, permitParams);
    }

    private PermitEntityDto createPermitEntityDto(final Long accountId, 
                                                  final Permit permit, 
                                                  final PermitType permitType,
                                                  final LocalDate activationDate,
                                                  final SortedMap<String, BigDecimal> annualEmissionsTargets,
                                                  final Map<UUID, String> attachments,
                                                  final int newConsolidationNumber) {
        
        // in case of variation the permit id exists, otherwise it has to be generated
        final String permitId = permitQueryService.getPermitIdByAccountId(accountId).orElse(
            generator.generate(accountId)
        );
        final InstallationOperatorDetails installationOperatorDetails = 
            installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId);
        
        return PermitEntityDto.builder()
            .id(permitId)
            .permitContainer(
                PermitContainer.builder()
                    .permit(permit)
                    .permitType(permitType)
                    .installationOperatorDetails(installationOperatorDetails)
                    .activationDate(activationDate)
                    .permitAttachments(attachments)
                    .annualEmissionsTargets(annualEmissionsTargets)
                    .build())
            .consolidationNumber(newConsolidationNumber)
            .accountId(accountId)
            .build();
    }

    private TemplateParams constructTemplateParams(final Request request, 
                                                   final String signatory,
                                                   final PermitEntityDto permitEntityDto, 
                                                   final PermitIssuanceRequestMetadata issuanceRequestMetadata,
                                                   final List<PermitVariationRequestInfo> variationRequestInfoList) {

        final TemplateParams templateParams = permitParamsProvider.constructTemplateParams(
            DocumentTemplatePermitParamsSourceData.builder()
                .request(request)
                .signatory(signatory)
                .permitContainer(permitEntityDto.getPermitContainer())
                .consolidationNumber(permitEntityDto.getConsolidationNumber())
                .issuanceRequestMetadata(issuanceRequestMetadata)
                .variationRequestInfoList(variationRequestInfoList).build());
        
        // permit id has to be set explicitly as it might not exist in the database yet (e.g. permit issuance)
        templateParams.setPermitId(permitEntityDto.getId());
        
        return templateParams;
    }

    private void updateAccountParams(final Permit permit, final PermitType permitType, final TemplateParams permitParams) {

        final InstallationAccountTemplateParams accountParams = (InstallationAccountTemplateParams) permitParams.getAccountParams();

        final EmitterType emitterType = switch (permitType) {
            case HSE -> EmitterType.HSE;
            case GHGE -> EmitterType.GHGE;
            case WASTE -> EmitterType.WASTE;
            case null -> EmitterType.GHGE;
        };
        accountParams.setEmitterType(emitterType.name());

        final BigDecimal quantity = permit.getEstimatedAnnualEmissions().getQuantity();
        final InstallationCategory installationCategory =
            InstallationCategoryMapper.getInstallationCategory(emitterType, quantity);
        accountParams.setInstallationCategory(installationCategory.getDescription());
    }
}
