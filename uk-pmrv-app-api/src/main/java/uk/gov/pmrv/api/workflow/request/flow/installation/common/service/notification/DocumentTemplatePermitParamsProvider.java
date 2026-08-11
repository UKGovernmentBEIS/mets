package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorStandardReferenceSourceType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.Transfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationValidityPeriod;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueChangesDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestMetadata;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTemplatePermitParamsProvider {

    private final InstallationDocumentTemplateCommonParamsProvider commonParamsProvider;
    private final RequestQueryService requestQueryService;
    private final Comparator<PermitVariationRequestInfo> comparator = Comparator.comparing(PermitVariationRequestInfo::getEndDate, Comparator.nullsLast(LocalDateTime::compareTo))
        .thenComparing(PermitVariationRequestInfo::getSubmissionDate, Comparator.nullsLast(LocalDateTime::compareTo));
    private final ConfigurationService configurationService;
    private static final String CBAM_TRANSITION_TOGGLE = "sub_installation_types.cbam.transition.toggle";

    public TemplateParams constructTemplateParams(final DocumentTemplatePermitParamsSourceData sourceData) {
        final Request request = sourceData.getRequest();
        final String signatory = sourceData.getSignatory();
        final TemplateParams templateParams = commonParamsProvider.constructCommonTemplateParams(request, signatory);

        final List<Request> reissueRequests = requestQueryService
                .findRequestsByAccountIdAndType(request.getAccountId(), RequestType.PERMIT_REISSUE);

        final PermitContainer permitContainer = sourceData.getPermitContainer();
        final int consolidationNumber = sourceData.getConsolidationNumber();
        final PermitIssuanceRequestMetadata issuanceMetadata = sourceData.getIssuanceRequestMetadata();
        List<PermitVariationRequestInfo> allPermitChangesRequestInfoList = new ArrayList<>();
        final List<PermitVariationRequestInfo> variationRequestInfoList = sourceData.getVariationRequestInfoList();
        final List<PermitVariationRequestInfo> reissueRequestsInfoList = reissueRequests.stream().map(r-> PermitVariationRequestInfo
                .builder()
                .id(r.getId())
                .changeType("Batch Variation")
                .submissionDate(r.getSubmissionDate())
                // if end date is null and batch reissue is still in progress then it's the current batch re-issue to which we add now as end date
                .endDate(r.getEndDate() != null || !RequestStatus.IN_PROGRESS.equals(r.getStatus()) ? r.getEndDate() : LocalDateTime.now())
                .metadata(PermitVariationRequestMetadata
                        .builder()
                        .logChanges(
                            Optional.of(r)
                                .map(Request::getMetadata)
                                .filter(metadata -> metadata instanceof ReissueRequestMetadata)
                                .map(metadata -> (ReissueRequestMetadata) metadata)
                                .map(ReissueRequestMetadata::getChangesDetails)
                                .filter(changesDetails -> changesDetails instanceof PermitBatchReissueChangesDetails)
                                .map(changesDetails -> (PermitBatchReissueChangesDetails) changesDetails)
                                .map(PermitBatchReissueChangesDetails::getChangesSummary)
                                .orElse(null)
                        )
                        .build())
                .build()).toList();

        allPermitChangesRequestInfoList.addAll(variationRequestInfoList);
        allPermitChangesRequestInfoList.addAll(reissueRequestsInfoList);

        allPermitChangesRequestInfoList.sort(comparator);

        final Map<String, List<ReferenceSource>> referenceSources =
            this.constructReferenceSources(permitContainer.getPermit());
        final Map<String, List<AnalysisMethod>> analysisMethods =
            this.constructAnalysisMethods(permitContainer.getPermit());

        List<Transfer> transfers = extractTransfers(permitContainer.getPermit());

        Optional<ConfigurationDTO> cbamTransitionToggleConfiguration = configurationService
                .getConfigurationByKey(CBAM_TRANSITION_TOGGLE);

        boolean cbamTransitionToggle = cbamTransitionToggleConfiguration
                .map(ConfigurationDTO::getValue)
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .orElse(false);

        boolean cbamTransition = SubInstallationValidityPeriod.FROM_01_2027.isValid(cbamTransitionToggle);

        return templateParams.withParams(Map.of(
                "permitContainer", permitContainer,
                "issuanceMetadata", issuanceMetadata,
                "variationRequestInfoList", allPermitChangesRequestInfoList,
                "consolidationNumber", consolidationNumber,
                "referenceSources", referenceSources,
                "analysisMethods", analysisMethods,
                "transfers", transfers,
                "documentIsDraft",signatory==null,
                "cbamTransition", cbamTransition
            )
        );
    }

    private List<Transfer> extractTransfers(final Permit permit) {
        if (permit == null || permit.getMonitoringApproaches() == null
                || permit.getMonitoringApproaches().getMonitoringApproaches() == null) {
            return Collections.emptyList();
        }

        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
                permit.getMonitoringApproaches().getMonitoringApproaches();

        List<Transfer> transfers = new ArrayList<>();
        if (monitoringApproaches.containsKey(MonitoringApproachType.CALCULATION_CO2)) {
            transfers.addAll(((CalculationOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.CALCULATION_CO2))
                    .getSourceStreamCategoryAppliedTiers()
                    .stream()
                    .map(tier -> tier.getSourceStreamCategory().getTransfer())
                    .filter(Objects::nonNull)
                    .filter(transfer -> Boolean.TRUE.equals(transfer.getEntryAccountingForTransfer()))
                    .toList());
        }
        if (monitoringApproaches.containsKey(MonitoringApproachType.MEASUREMENT_CO2)) {
            transfers.addAll(((MeasurementOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT_CO2))
                    .getEmissionPointCategoryAppliedTiers()
                    .stream()
                    .map(tier -> tier.getEmissionPointCategory().getTransfer())
                    .filter(Objects::nonNull)
                    .filter(transfer -> Boolean.TRUE.equals(transfer.getEntryAccountingForTransfer()))
                    .toList());
        }
        if (monitoringApproaches.containsKey(MonitoringApproachType.MEASUREMENT_N2O)) {
            transfers.addAll(((MeasurementOfN2OMonitoringApproach) monitoringApproaches.get(MonitoringApproachType.MEASUREMENT_N2O))
                    .getEmissionPointCategoryAppliedTiers()
                    .stream()
                    .map(tier -> tier.getEmissionPointCategory().getTransfer())
                    .filter(Objects::nonNull)
                    .filter(transfer -> Boolean.TRUE.equals(transfer.getEntryAccountingForTransfer()))
                    .toList());
        }
        return transfers;
    }

    private Map<String, List<ReferenceSource>> constructReferenceSources(final Permit permit) {
        if (permit == null || permit.getSourceStreams() == null || permit.getEmissionSources() == null
                || permit.getMonitoringApproaches() == null || permit.getMonitoringApproaches().getMonitoringApproaches() == null) {
            return Collections.emptyMap();
        }

        final List<SourceStream> allSourceStreams = permit.getSourceStreams().getSourceStreams();
        final List<EmissionSource> allEmissionSources = permit.getEmissionSources().getEmissionSources();

        if (allSourceStreams == null || allEmissionSources == null) {
            return Collections.emptyMap();
        }

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
                permit.getMonitoringApproaches().getMonitoringApproaches();
        final CalculationOfCO2MonitoringApproach calculation =
                (CalculationOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.CALCULATION_CO2);
        final List<ReferenceSource> calculationReferenceSources = new ArrayList<>();
        if (calculation != null && calculation.getSourceStreamCategoryAppliedTiers() != null) {
            calculation.getSourceStreamCategoryAppliedTiers().forEach(ss -> {

                if (ss == null || ss.getSourceStreamCategory() == null) return;

                final String sourceStream = ss.getSourceStreamCategory().getSourceStream();
                final Set<String> emissionSources = ss.getSourceStreamCategory().getEmissionSources();
                final String sourceStreamRef = allSourceStreams.stream()
                        .filter(s -> s.getId().equals(sourceStream))
                        .findFirst()
                        .map(SourceStream::getReference)
                        .orElse(null);
                if (sourceStreamRef == null) return;

                final Set<String> emissionSourcesRefs = emissionSources != null
                        ? emissionSources.stream()
                          .map(e -> allEmissionSources.stream()
                                    .filter(es -> es.getId().equals(e))
                                    .findFirst()
                                    .map(EmissionSource::getReference)
                                    .orElse(null))
                          .filter(Objects::nonNull)
                          .collect(Collectors.toSet())
                        : Collections.emptySet();

                if (ss.getNetCalorificValue() != null && ss.getNetCalorificValue().isExist()
                        && Boolean.TRUE.equals(ss.getNetCalorificValue().getDefaultValueApplied())
                        && ss.getNetCalorificValue().getStandardReferenceSource() != null) {
                    final ReferenceSource ncvRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.NCV)
                            .type(
                                    ss.getNetCalorificValue().getStandardReferenceSource().getType() !=
                                            CalculationNetCalorificValueStandardReferenceSourceType.OTHER ?
                                            ss.getNetCalorificValue().getStandardReferenceSource().getType().getDescription() :
                                            ss.getNetCalorificValue().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getNetCalorificValue().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(ncvRef);
                }

                if (ss.getEmissionFactor() != null && ss.getEmissionFactor().isExist()
                        && Boolean.TRUE.equals(ss.getEmissionFactor().getDefaultValueApplied())
                        && ss.getEmissionFactor().getStandardReferenceSource() != null) {
                    final ReferenceSource efRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.EF)
                            .type(
                                    ss.getEmissionFactor().getStandardReferenceSource().getType() !=
                                            CalculationEmissionFactorStandardReferenceSourceType.OTHER ?
                                            ss.getEmissionFactor().getStandardReferenceSource().getType().getDescription() :
                                            ss.getEmissionFactor().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getEmissionFactor().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(efRef);
                }

                if (ss.getOxidationFactor() != null && ss.getOxidationFactor().isExist()
                        && Boolean.TRUE.equals(ss.getOxidationFactor().getDefaultValueApplied())
                        && ss.getOxidationFactor().getStandardReferenceSource() != null) {
                    final ReferenceSource oxfRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.OxF)
                            .type(
                                    ss.getOxidationFactor().getStandardReferenceSource().getType() !=
                                            CalculationOxidationFactorStandardReferenceSourceType.OTHER ?
                                            ss.getOxidationFactor().getStandardReferenceSource().getType().getDescription() :
                                            ss.getOxidationFactor().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getOxidationFactor().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(oxfRef);
                }

                if (ss.getCarbonContent() != null && ss.getCarbonContent().isExist()
                        && Boolean.TRUE.equals(ss.getCarbonContent().getDefaultValueApplied())
                        && ss.getCarbonContent().getStandardReferenceSource() != null) {
                    final ReferenceSource ccRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.CC)
                            .type(
                                    ss.getCarbonContent().getStandardReferenceSource().getType() !=
                                            CalculationCarbonContentStandardReferenceSourceType.OTHER ?
                                            ss.getCarbonContent().getStandardReferenceSource().getType().getDescription() :
                                            ss.getCarbonContent().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getCarbonContent().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(ccRef);
                }

                if (ss.getConversionFactor() != null && ss.getConversionFactor().isExist()
                        && Boolean.TRUE.equals(ss.getConversionFactor().getDefaultValueApplied())
                        && ss.getConversionFactor().getStandardReferenceSource() != null) {
                    final ReferenceSource cfRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.CF)
                            .type(
                                    ss.getConversionFactor().getStandardReferenceSource().getType() !=
                                            CalculationConversionFactorStandardReferenceSourceType.OTHER ?
                                            ss.getConversionFactor().getStandardReferenceSource().getType().getDescription() :
                                            ss.getConversionFactor().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getConversionFactor().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(cfRef);
                }

                if (ss.getBiomassFraction() != null && ss.getBiomassFraction().isExist()
                        && Boolean.TRUE.equals(ss.getBiomassFraction().getDefaultValueApplied())
                        && ss.getBiomassFraction().getStandardReferenceSource() != null) {
                    final ReferenceSource bfRef = ReferenceSource.builder()
                            .sourceStream(sourceStreamRef)
                            .emissionSources(emissionSourcesRefs)
                            .parameter(Parameter.BF)
                            .type(
                                    ss.getBiomassFraction().getStandardReferenceSource().getType() !=
                                            CalculationBiomassFractionStandardReferenceSourceType.OTHER ?
                                            ss.getBiomassFraction().getStandardReferenceSource().getType().getDescription() :
                                            ss.getBiomassFraction().getStandardReferenceSource().getOtherTypeDetails()
                            )
                            .defaultValue(ss.getBiomassFraction().getStandardReferenceSource().getDefaultValue())
                            .build();
                    calculationReferenceSources.add(bfRef);
                }
            });
        }
        return Map.of(MonitoringApproachType.CALCULATION_CO2.name(), calculationReferenceSources);
    }

    private Map<String, List<AnalysisMethod>> constructAnalysisMethods(final Permit permit) {

        if (permit == null || permit.getSourceStreams() == null || permit.getEmissionSources() == null
                || permit.getMonitoringApproaches() == null || permit.getMonitoringApproaches().getMonitoringApproaches() == null) {
            return Collections.emptyMap();
        }

        final List<SourceStream> allSourceStreams = permit.getSourceStreams().getSourceStreams();
        final List<EmissionSource> allEmissionSources = permit.getEmissionSources().getEmissionSources();

        if (allSourceStreams == null || allEmissionSources == null) {
            return Collections.emptyMap();
        }

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
                permit.getMonitoringApproaches().getMonitoringApproaches();
        final CalculationOfCO2MonitoringApproach calculation =
                (CalculationOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.CALCULATION_CO2);
        final List<AnalysisMethod> calculationAnalysisMethods = new ArrayList<>();
        if (calculation != null && calculation.getSourceStreamCategoryAppliedTiers() != null) {
            calculation.getSourceStreamCategoryAppliedTiers().forEach(ss -> {

                if (ss == null || ss.getSourceStreamCategory() == null) return;

                final String sourceStream = ss.getSourceStreamCategory().getSourceStream();
                final Set<String> emissionSources = ss.getSourceStreamCategory().getEmissionSources();
                final String sourceStreamRef = allSourceStreams.stream()
                        .filter(s -> s.getId().equals(sourceStream))
                        .findFirst()
                        .map(SourceStream::getReference)
                        .orElse(null);
                if (sourceStreamRef == null) return;

                final Set<String> emissionSourcesRefs = emissionSources != null
                        ? emissionSources.stream()
                          .map(e -> allEmissionSources.stream()
                                    .filter(es -> es.getId().equals(e))
                                    .findFirst()
                                    .map(EmissionSource::getReference)
                                    .orElse(null))
                          .filter(Objects::nonNull)
                          .collect(Collectors.toSet())
                        : Collections.emptySet();

                if (ss.getNetCalorificValue() != null && ss.getNetCalorificValue().isExist()
                        && ss.getNetCalorificValue().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getNetCalorificValue().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getNetCalorificValue().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getNetCalorificValue().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod ncvAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.NCV)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(ncvAnalysis);
                    });
                }

                if (ss.getEmissionFactor() != null && ss.getEmissionFactor().isExist()
                        && ss.getEmissionFactor().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getEmissionFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getEmissionFactor().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getEmissionFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod efAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.EF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(efAnalysis);
                    });
                }

                if (ss.getOxidationFactor() != null && ss.getOxidationFactor().isExist()
                        && ss.getOxidationFactor().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getOxidationFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getOxidationFactor().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getOxidationFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod oxfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.OxF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(oxfAnalysis);
                    });
                }

                if (ss.getCarbonContent() != null && ss.getCarbonContent().isExist()
                        && ss.getCarbonContent().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getCarbonContent().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getCarbonContent().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getCarbonContent().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod ccAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.CC)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(ccAnalysis);
                    });
                }

                if (ss.getConversionFactor() != null && ss.getConversionFactor().isExist()
                        && ss.getConversionFactor().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getConversionFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getConversionFactor().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getConversionFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod cfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.CF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(cfAnalysis);
                    });
                }

                if (ss.getBiomassFraction() != null && ss.getBiomassFraction().isExist()
                        && ss.getBiomassFraction().getCalculationAnalysisMethodData() != null
                        && Boolean.TRUE.equals(ss.getBiomassFraction().getCalculationAnalysisMethodData().getAnalysisMethodUsed())
                        && ss.getBiomassFraction().getCalculationAnalysisMethodData().getAnalysisMethods() != null) {
                    ss.getBiomassFraction().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                        final AnalysisMethod bfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.BF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                        m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryName() : null)
                                .laboratoryAccredited(m.getLaboratory() != null ? m.getLaboratory().getLaboratoryAccredited() : null)
                                .build();
                        calculationAnalysisMethods.add(bfAnalysis);
                    });
                }
            });
        }
        return Map.of(MonitoringApproachType.CALCULATION_CO2.name(), calculationAnalysisMethods);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReferenceSource {

        private String sourceStream;
        private Set<String> emissionSources;
        private Parameter parameter;
        private String type;
        private String defaultValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisMethod {

        private String sourceStream;
        private Set<String> emissionSources;
        private Parameter parameter;
        private String analysis;
        private String samplingFrequency;
        private String laboratoryName;
        private Boolean laboratoryAccredited;
    }

    public enum Parameter {
        NCV,
        EF,
        OxF,
        CC,
        CF,
        BF,
    }
}