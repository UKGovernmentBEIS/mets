package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.permit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityDto;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.service.PermitIdentifierGenerator;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification.DocumentTemplatePermitParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.service.PermitIssuanceRequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermitPreviewCreatePermitDocumentServiceTest {
	
	@InjectMocks
    private PermitPreviewCreatePermitDocumentService service;

	@Mock
	private DocumentTemplatePermitParamsProvider permitParamsProvider;

	@Mock
	private PermitQueryService permitQueryService;

	@Mock
	private PermitIssuanceRequestQueryService permitIssuanceRequestQueryService;

	@Mock
	private PermitCreateDocumentService permitCreateDocumentService;

	@Mock
	private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	
	@Mock
	private	PermitIdentifierGenerator generator;
	
	@Test
	void createPermitForVariation() {

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
		final InstallationOperatorDetails operatorDetails = InstallationOperatorDetails.builder().build();
		final LocalDateTime now = LocalDateTime.of(2023, 1, 2, 3, 4);
		final int consolidationNumber = 2;
		final String permitId = "permitId";
		final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
		final int newConsolidationNumber = consolidationNumber + 1;
		final TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>(Map.of("2022", new BigDecimal(12345)));
		final PermitEntityDto permitEntityDto = PermitEntityDto.builder()
			.id(permitId)
			.accountId(accountId)
			.permitContainer(PermitContainer.builder()
				.permitType(permitType).permit(
					permit)
				.activationDate(activationDate)
				.installationOperatorDetails(operatorDetails)
				.permitAttachments(attachments)
				.annualEmissionsTargets(annualEmissionsTargets)
				.build()
			)
			.consolidationNumber(newConsolidationNumber)
			.build();
		final PermitIssuanceRequestMetadata
			issuanceRequestMetadata = PermitIssuanceRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build();
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
		final TemplateParams templateParams = TemplateParams.builder()
			.accountParams(InstallationAccountTemplateParams.builder().build())
			.build();
		final DocumentTemplatePermitParamsSourceData sourceData = DocumentTemplatePermitParamsSourceData.builder()
			.request(request)
			.signatory(signatory)
			.permitContainer(permitEntityDto.getPermitContainer())
			.consolidationNumber(permitEntityDto.getConsolidationNumber())
			.issuanceRequestMetadata(issuanceRequestMetadata)
			.variationRequestInfoList(allVariations)
			.build();
		final String fileName = "fileName";
		final FileDTO fileDTO = FileDTO.builder().fileName(fileName).build();

		when(permitQueryService.getPermitIdByAccountId(accountId)).thenReturn(Optional.of(permitId));
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(
			operatorDetails
		);
		when(permitIssuanceRequestQueryService.findPermitIssuanceMetadataByAccountId(
			accountId)).thenReturn(issuanceRequestMetadata
		);
		when(permitParamsProvider.constructTemplateParams(sourceData)).thenReturn(templateParams);
		when(permitCreateDocumentService.generateDocumentWithParams(permitEntityDto, templateParams)).thenReturn(
			fileDTO
		);

		final FileDTO result = service.getFile(decisionNotification, request, accountId, permit, permitType, activationDate, annualEmissionsTargets, attachments, newConsolidationNumber, allVariations);

		assertEquals(result.getFileName(), fileName);
		assertEquals(((InstallationAccountTemplateParams)templateParams.getAccountParams()).getEmitterType(),
			EmitterType.GHGE.name());
		assertEquals(((InstallationAccountTemplateParams)templateParams.getAccountParams()).getInstallationCategory(),
			InstallationCategory.A_LOW_EMITTER.getDescription());
	}
}