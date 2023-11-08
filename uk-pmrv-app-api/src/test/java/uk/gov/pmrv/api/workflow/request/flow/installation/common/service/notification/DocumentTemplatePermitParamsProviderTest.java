package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityDTO;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.CompetentAuthorityTemplateParams;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.SignatoryTemplateParams;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationAnalysisMethod;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationLaboratory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueStandardReferenceSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetailsType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationEmitter;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeteringUncertainty;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentTemplatePermitParamsProviderTest {

    @InjectMocks
    private DocumentTemplatePermitParamsProvider provider;

    @Mock
    private InstallationDocumentTemplateCommonParamsProvider commonParamsProvider;

    @Test
    void constructTemplateParams() throws IOException {

        final LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 1, 1, 1);
        final Long accountId = 1L;
        final PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .build();
        final Request request = Request.builder().id("1").accountId(accountId)
            .type(RequestType.PERMIT_ISSUANCE)
            .submissionDate(submissionDate)
            .payload(requestPayload)
            .build();
        final InstallationAccountDTO account = InstallationAccountDTO.builder()
            .id(accountId)
            .name("accountname")
            .emitterType(EmitterType.GHGE)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .siteName("accountsitename")
            .location(LocationOnShoreDTO.builder()
                .type(LocationType.ONSHORE)
                .gridReference("gridRef")
                .address(AddressDTO.builder()
                    .line1("line1")
                    .line2("line2")
                    .city("city")
                    .country("GR")
                    .postcode("15125")
                    .build())
                .build())
            .legalEntity(LegalEntityDTO.builder()
                .name("lename")
                .address(AddressDTO.builder()
                    .line1("le_line1")
                    .line2("le_line2")
                    .city("le_city")
                    .country("GR")
                    .postcode("15125")
                    .build())
                .build())
            .build();
        final String signatory = "signatoryUserId";
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder()
            .firstName("fn").lastName("ln").email("email@email")
            .build();
        final String permitId = "permitId";
        final TransferCO2 transfer = TransferCO2.builder()
            .transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION)
            .entryAccountingForTransfer(Boolean.TRUE)
            .installationDetailsType(InstallationDetailsType.INSTALLATION_EMITTER)
            .installationEmitter(InstallationEmitter.builder()
                .emitterId("EmitterId")
                .email("test@test.com")
                .build())
            .build();
        final PermitContainer permitContainer = PermitContainer.builder().permitType(PermitType.GHGE).permit(Permit.builder()
                .sourceStreams(SourceStreams.builder().sourceStreams(List.of(
                    SourceStream.builder().id("ss1").type(SourceStreamType.AMMONIA_FUEL_AS_PROCESS_INPUT).description(
                        SourceStreamDescription.ACETYLENE).reference("ss ref1").build(),
                    SourceStream.builder().id("ss2").type(SourceStreamType.COMBUSTION_FLARES)
                        .description(SourceStreamDescription.BIOGASOLINE).reference("ss ref2").build()
                )).build())
                .emissionSources(EmissionSources.builder().emissionSources(List.of(
                    EmissionSource.builder().id("es1").description("source desc 1").reference("source ref 1").build(),
                    EmissionSource.builder().id("es2").description("source desc 2").reference("source ref 2").build()
                )).build())
                .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(
                        EmissionPoint.builder().id("ep1").description("point desc 1").reference("point ref 1").build(),
                        EmissionPoint.builder().id("ep2").description("point desc 2").reference("point ref 2").build()
                    )
                ).build())
                .monitoringApproaches(
                    MonitoringApproaches.builder().monitoringApproaches(Map.of(
                        MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder()
                            .approachDescription("approach description")
                            .type(MonitoringApproachType.CALCULATION_CO2)
                            .sourceStreamCategoryAppliedTiers(List.of(
                                CalculationSourceStreamCategoryAppliedTier.builder()
                                    .sourceStreamCategory(
                                        CalculationSourceStreamCategory.builder()
                                            .sourceStream("ss1")
                                            .emissionSources(Set.of("es2", "es1"))
                                            .transfer(transfer)
                                            .categoryType(CategoryType.DE_MINIMIS).build())
                                    .activityData(
                                        CalculationActivityData.builder().tier(CalculationActivityDataTier.TIER_3)
                                            .measurementDevicesOrMethods(Set.of("md id 1", "md id 2")).uncertainty(
                                                MeteringUncertainty.LESS_OR_EQUAL_5_0).build())
                                    .netCalorificValue(CalculationNetCalorificValue.builder()
                                        .exist(true)
                                        .defaultValueApplied(true)
                                        .standardReferenceSource(
                                            CalculationNetCalorificValueStandardReferenceSource.builder()
                                                .type(
                                                    CalculationNetCalorificValueStandardReferenceSourceType.CONSERVATIVE_ESTIMATION)
                                                .defaultValue("net cal value")
                                                .build())
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationNetCalorificValueTier.TIER_1).build())
                                    .emissionFactor(CalculationEmissionFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(true)
                                        .standardReferenceSource(
                                            CalculationEmissionFactorStandardReferenceSource.builder()
                                                .type(
                                                    CalculationEmissionFactorStandardReferenceSourceType.BRITISH_CERAMIC_CONFEDERATION)
                                                .defaultValue("ef value")
                                                .build())
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationEmissionFactorTier.TIER_1).build())
                                    .oxidationFactor(CalculationOxidationFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                            .analysisMethodUsed(true)
                                            .analysisMethods(List.of(
                                                CalculationAnalysisMethod.builder()
                                                    .analysis("analysis 1")
                                                    .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS)
                                                    .laboratory(CalculationLaboratory.builder()
                                                        .laboratoryName("lab 1")
                                                        .laboratoryAccredited(true)
                                                        .build())
                                                    .build(),
                                                CalculationAnalysisMethod.builder()
                                                    .analysis("analysis 2")
                                                    .samplingFrequency(CalculationSamplingFrequency.BI_ANNUALLY)
                                                    .laboratory(CalculationLaboratory.builder()
                                                        .laboratoryName("lab 2")
                                                        .laboratoryAccredited(true)
                                                        .build())
                                                    .build()
                                            ))
                                            .build())
                                        .tier(CalculationOxidationFactorTier.TIER_3).build())
                                    .carbonContent(CalculationCarbonContent.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationCarbonContentTier.TIER_1).build())
                                    .conversionFactor(CalculationConversionFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationConversionFactorTier.TIER_2).build())
                                    .biomassFraction(CalculationBiomassFraction.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationBiomassFractionTier.TIER_2).build())
                                    .build(),
                                CalculationSourceStreamCategoryAppliedTier.builder()
                                    .sourceStreamCategory(
                                        CalculationSourceStreamCategory.builder()
                                            .sourceStream("ss2")
                                            .emissionSources(Set.of("es1"))
                                            .categoryType(CategoryType.MAJOR).build())
                                    .activityData(
                                        CalculationActivityData.builder().tier(CalculationActivityDataTier.TIER_3)
                                            .measurementDevicesOrMethods(Set.of("md id 2")).uncertainty(
                                                MeteringUncertainty.LESS_OR_EQUAL_2_5).build())
                                    .netCalorificValue(CalculationNetCalorificValue.builder()
                                        .exist(true)
                                        .defaultValueApplied(true)
                                        .standardReferenceSource(
                                            CalculationNetCalorificValueStandardReferenceSource.builder()
                                                .type(
                                                    CalculationNetCalorificValueStandardReferenceSourceType.JEP_GUIDANCE_FOR_MONITORING_AND_REPORTING)
                                                .defaultValue("net cal value 2")
                                                .build())
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationNetCalorificValueTier.TIER_1).build())
                                    .emissionFactor(CalculationEmissionFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationEmissionFactorTier.TIER_1).build())
                                    .oxidationFactor(CalculationOxidationFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationOxidationFactorTier.TIER_3).build())
                                    .carbonContent(CalculationCarbonContent.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder()
                                            .analysisMethodUsed(true)
                                            .analysisMethods(List.of(
                                                CalculationAnalysisMethod.builder()
                                                    .analysis("analysis 3")
                                                    .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS)
                                                    .laboratory(CalculationLaboratory.builder()
                                                        .laboratoryName("lab 3")
                                                        .laboratoryAccredited(false)
                                                        .build())
                                                    .build()
                                            ))
                                            .build())
                                        .tier(CalculationCarbonContentTier.TIER_1).build())
                                    .conversionFactor(CalculationConversionFactor.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationConversionFactorTier.TIER_2).build())
                                    .biomassFraction(CalculationBiomassFraction.builder()
                                        .exist(true)
                                        .defaultValueApplied(false)
                                        .calculationAnalysisMethodData(CalculationAnalysisMethodData.builder().analysisMethodUsed(false).build())
                                        .tier(CalculationBiomassFractionTier.TIER_2).build())
                                    .build()

                            ))
                            .build()
                    )).build())
                .build()).build();
        final List<LocalDateTime> rfiResponseDates = List.of(
            LocalDateTime.of(2022, 1, 1, 1, 1),
            LocalDateTime.of(2022, 2, 2, 2, 2));
        final PermitIssuanceRequestMetadata issuanceMetadata =
            PermitIssuanceRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build();
		final List<PermitVariationRequestInfo> variationRequestInfoList = List.of(
				PermitVariationRequestInfo.builder()
					.metadata(PermitVariationRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build()).build(),
				PermitVariationRequestInfo.builder()
					.metadata(PermitVariationRequestMetadata.builder().rfiResponseDates(rfiResponseDates).build()).build());
        final DocumentTemplatePermitParamsSourceData sourceParams = DocumentTemplatePermitParamsSourceData.builder()
            .request(request)
            .permitContainer(permitContainer)
            .consolidationNumber(6)
            .signatory(signatory)
            .issuanceRequestMetadata(issuanceMetadata)
            .variationRequestInfoList(variationRequestInfoList)
            .build();
        final String caCentralInfo = "ca central info";
        final RegulatorUserDTO signatoryUser = RegulatorUserDTO.builder()
            .firstName("signtoryFn").lastName("signatoryLn").jobTitle("signatoryJobTitle")
            .signature(FileInfoDTO.builder().name("signature.pdf").uuid(UUID.randomUUID().toString()).build())
            .build();
        final FileDTO signatorySignature = FileDTO.builder()
            .fileContent("content".getBytes())
            .fileName("signature")
            .fileSize("content".length())
            .fileType("type")
            .build();

        CompetentAuthorityDTO ca = CompetentAuthorityDTO.builder().id(CompetentAuthorityEnum.ENGLAND).build();

        final TemplateParams commonTemplateParams = TemplateParams.builder()
            .competentAuthorityParams(CompetentAuthorityTemplateParams.builder()
                .competentAuthority(ca)
                .logo(Files.readAllBytes(
                    Paths.get("src", "main", "resources", "images", "ca", CompetentAuthorityEnum.ENGLAND.getLogoPath())))
                .build())
            .competentAuthorityCentralInfo(caCentralInfo)
            .signatoryParams(SignatoryTemplateParams.builder()
                .fullName(signatoryUser.getFullName())
                .jobTitle(signatoryUser.getJobTitle())
                .signature(signatorySignature.getFileContent())
                .build())
            .accountParams(InstallationAccountTemplateParams.builder()
                .name(account.getName())
                .siteName(account.getSiteName())
                .emitterType(account.getEmitterType().name())
                .location("accountLocation")
                .legalEntityName(account.getLegalEntity().getName())
                .legalEntityLocation("leLocation")
                .primaryContact(accountPrimaryContact.getFullName())
                .serviceContact("service fn service ln")
                .serviceContactEmail("serviceContact@email.com")
                .build())
            .permitId(permitId)
            .build();
        final Map<String, List<DocumentTemplatePermitParamsProvider.ReferenceSource>> referenceSources = new HashMap<>();
        referenceSources.put(MonitoringApproachType.CALCULATION_CO2.name(), List.of(
            DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                .sourceStream("ss ref1")
                .emissionSources(Set.of("source ref 1", "source ref 2"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.NCV)
                .type("Conservative estimation")
                .defaultValue("net cal value")
                .build(),
            DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                .sourceStream("ss ref1")
                .emissionSources(Set.of("source ref 1", "source ref 2"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.EF)
                .type("British Ceramic Confederation (BCC) Methodology (latest version)")
                .defaultValue("ef value")
                .build(),
            DocumentTemplatePermitParamsProvider.ReferenceSource.builder()
                .sourceStream("ss ref2")
                .emissionSources(Set.of("source ref 1"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.NCV)
                .type("JEP Guidance for Monitoring and Reporting of CO2 from Power Stations")
                .defaultValue("net cal value 2")
                .build()
        ));
        final Map<String, List<DocumentTemplatePermitParamsProvider.AnalysisMethod>> analysisMethods = new HashMap<>();
        analysisMethods.put(MonitoringApproachType.CALCULATION_CO2.name(), List.of(
            DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                .sourceStream("ss ref1")
                .emissionSources(Set.of("source ref 1", "source ref 2"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.OxF)
                .analysis("analysis 1")
                .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS.getDescription())
                .laboratoryName("lab 1")
                .laboratoryAccredited(true)
                .build(),
            DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                .sourceStream("ss ref1")
                .emissionSources(Set.of("source ref 1", "source ref 2"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.OxF)
                .analysis("analysis 2")
                .samplingFrequency(CalculationSamplingFrequency.BI_ANNUALLY.getDescription())
                .laboratoryName("lab 2")
                .laboratoryAccredited(true)
                .build(),
            DocumentTemplatePermitParamsProvider.AnalysisMethod.builder()
                .sourceStream("ss ref2")
                .emissionSources(Set.of("source ref 1"))
                .parameter(DocumentTemplatePermitParamsProvider.Parameter.CC)
                .analysis("analysis 3")
                .samplingFrequency(CalculationSamplingFrequency.CONTINUOUS.getDescription())
                .laboratoryName("lab 3")
                .laboratoryAccredited(false)
                .build()
        ));

        when(commonParamsProvider.constructCommonTemplateParams(request, signatory)).thenReturn(commonTemplateParams);

        //invoke
        TemplateParams result = provider.constructTemplateParams(sourceParams);

        assertThat(result).isEqualTo(commonTemplateParams
            .withParams(Map.of(
                "permitContainer", permitContainer,
                "consolidationNumber", 6,
                "issuanceMetadata", issuanceMetadata,
                "variationRequestInfoList", variationRequestInfoList,
                "referenceSources", referenceSources,
                "analysisMethods", analysisMethods,
                "transfers", List.of(transfer)
                )
            )
        );

        verify(commonParamsProvider, times(1)).constructCommonTemplateParams(request, signatory);
    }
}
