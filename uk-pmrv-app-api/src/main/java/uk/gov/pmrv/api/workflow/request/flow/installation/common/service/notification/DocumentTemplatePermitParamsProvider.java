package uk.gov.pmrv.api.workflow.request.flow.installation.common.service.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.common.domain.PermitIssuanceRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationRequestInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentTemplatePermitParamsProvider {

    private final InstallationDocumentTemplateCommonParamsProvider commonParamsProvider;

    public TemplateParams constructTemplateParams(final DocumentTemplatePermitParamsSourceData sourceData) {

        final Request request = sourceData.getRequest();
        final String signatory = sourceData.getSignatory();
        final TemplateParams templateParams = commonParamsProvider.constructCommonTemplateParams(request, signatory);

        final PermitContainer permitContainer = sourceData.getPermitContainer();
        final int consolidationNumber = sourceData.getConsolidationNumber();
        final PermitIssuanceRequestMetadata issuanceMetadata = sourceData.getIssuanceRequestMetadata();
        final List<PermitVariationRequestInfo> variationRequestInfoList = sourceData.getVariationRequestInfoList();

        final Map<String, List<ReferenceSource>> referenceSources =
            this.constructReferenceSources(permitContainer.getPermit());
        final Map<String, List<AnalysisMethod>> analysisMethods =
            this.constructAnalysisMethods(permitContainer.getPermit());

        Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            permitContainer.getPermit().getMonitoringApproaches().getMonitoringApproaches();

        List<Transfer> transfers = extractTransfers(monitoringApproaches);

        return templateParams.withParams(Map.of(
                "permitContainer", permitContainer,
                "issuanceMetadata", issuanceMetadata,
                "variationRequestInfoList", variationRequestInfoList,
                "consolidationNumber", consolidationNumber,
                "referenceSources", referenceSources,
                "analysisMethods", analysisMethods,
                "transfers", transfers
            )
        );
    }

    private List<Transfer> extractTransfers(Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches) {
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

        final List<SourceStream> allSourceStreams = permit.getSourceStreams().getSourceStreams();
        final List<EmissionSource> allEmissionSources = permit.getEmissionSources().getEmissionSources();

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            permit.getMonitoringApproaches().getMonitoringApproaches();
        final CalculationOfCO2MonitoringApproach calculation =
            (CalculationOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.CALCULATION_CO2);
        final List<ReferenceSource> calculationReferenceSources = new ArrayList<>();
        if (calculation != null) {
            calculation.getSourceStreamCategoryAppliedTiers().forEach(ss -> {

                    final String sourceStream = ss.getSourceStreamCategory().getSourceStream();
                    final Set<String> emissionSources = ss.getSourceStreamCategory().getEmissionSources();
                    final String sourceStreamRef = allSourceStreams.stream()
                        .filter(s -> s.getId().equals(sourceStream))
                        .findFirst()
                        .get()
                        .getReference();
                    final Set<String> emissionSourcesRefs = emissionSources.stream()
                        .map(e -> allEmissionSources.stream()
                            .filter(es -> es.getId().equals(e))
                            .findFirst()
                            .get()
                            .getReference())
                        .collect(Collectors.toSet());

                    final boolean ncvHasDefaultValue =
                        ss.getNetCalorificValue().isExist() && ss.getNetCalorificValue().getDefaultValueApplied();
                    if (ncvHasDefaultValue) {
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

                    final boolean efHasDefaultValue =
                        ss.getEmissionFactor().isExist() && ss.getEmissionFactor().getDefaultValueApplied();
                    if (efHasDefaultValue) {
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

                    final boolean oxfHasDefaultValue =
                        ss.getOxidationFactor().isExist() && ss.getOxidationFactor().getDefaultValueApplied();
                    if (oxfHasDefaultValue) {
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

                    final boolean ccHasDefaultValue =
                        ss.getCarbonContent().isExist() && ss.getCarbonContent().getDefaultValueApplied();
                    if (ccHasDefaultValue) {
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

                    final boolean cfHasDefaultValue =
                        ss.getConversionFactor().isExist() && ss.getConversionFactor().getDefaultValueApplied();
                    if (cfHasDefaultValue) {
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

                    final boolean bfHasDefaultValue =
                        ss.getBiomassFraction().isExist() && ss.getBiomassFraction().getDefaultValueApplied();
                    if (bfHasDefaultValue) {
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
                }
            );
        }
        return Map.of(MonitoringApproachType.CALCULATION_CO2.name(), calculationReferenceSources);
    }

    private Map<String, List<AnalysisMethod>> constructAnalysisMethods(final Permit permit) {

        final List<SourceStream> allSourceStreams = permit.getSourceStreams().getSourceStreams();
        final List<EmissionSource> allEmissionSources = permit.getEmissionSources().getEmissionSources();

        final Map<MonitoringApproachType, PermitMonitoringApproachSection> monitoringApproaches =
            permit.getMonitoringApproaches().getMonitoringApproaches();
        final CalculationOfCO2MonitoringApproach calculation =
            (CalculationOfCO2MonitoringApproach) monitoringApproaches.get(MonitoringApproachType.CALCULATION_CO2);
        final List<AnalysisMethod> calculationAnalysisMethods = new ArrayList<>();
        if (calculation != null) {
            calculation.getSourceStreamCategoryAppliedTiers().forEach(ss -> {

                final String sourceStream = ss.getSourceStreamCategory().getSourceStream();
                final Set<String> emissionSources = ss.getSourceStreamCategory().getEmissionSources();
                final String sourceStreamRef = allSourceStreams.stream()
                    .filter(s -> s.getId().equals(sourceStream))
                    .findFirst()
                    .get()
                    .getReference();
                final Set<String> emissionSourcesRefs = emissionSources.stream()
                    .map(e -> allEmissionSources.stream()
                        .filter(es -> es.getId().equals(e))
                        .findFirst()
                        .get()
                        .getReference())
                    .collect(Collectors.toSet());

                final boolean ncvHasAnalysis = ss.getNetCalorificValue().isExist() &&
                    ss.getNetCalorificValue().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (ncvHasAnalysis) {
                    ss.getNetCalorificValue().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod ncvAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.NCV)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(ncvAnalysis);
                        }
                    );
                }

                final boolean efHasAnalysis = ss.getEmissionFactor().isExist() &&
                    ss.getEmissionFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (efHasAnalysis) {
                    ss.getEmissionFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod efAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.EF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(efAnalysis);
                        }
                    );
                }

                final boolean oxfHasAnalysis = ss.getOxidationFactor().isExist() &&
                    ss.getOxidationFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (oxfHasAnalysis) {
                    ss.getOxidationFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod oxfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.OxF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(oxfAnalysis);
                        }
                    );
                }

                final boolean ccHasAnalysis = ss.getCarbonContent().isExist() &&
                    ss.getCarbonContent().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (ccHasAnalysis) {
                    ss.getCarbonContent().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod ccAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.CC)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(ccAnalysis);
                        }
                    );
                }

                final boolean cfHasAnalysis = ss.getConversionFactor().isExist() &&
                    ss.getConversionFactor().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (cfHasAnalysis) {
                    ss.getConversionFactor().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod cfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.CF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(cfAnalysis);
                        }
                    );
                }

                final boolean bfHasAnalysis = ss.getBiomassFraction().isExist() &&
                    ss.getBiomassFraction().getCalculationAnalysisMethodData().getAnalysisMethodUsed();
                if (bfHasAnalysis) {
                    ss.getBiomassFraction().getCalculationAnalysisMethodData().getAnalysisMethods().forEach(m -> {
                            final AnalysisMethod bfAnalysis = AnalysisMethod.builder()
                                .sourceStream(sourceStreamRef)
                                .emissionSources(emissionSourcesRefs)
                                .parameter(Parameter.BF)
                                .analysis(m.getAnalysis())
                                .samplingFrequency(m.getSamplingFrequency() != CalculationSamplingFrequency.OTHER ?
                                    m.getSamplingFrequency().getDescription() : m.getSamplingFrequencyOtherDetails())
                                .laboratoryName(m.getLaboratory().getLaboratoryName())
                                .laboratoryAccredited(m.getLaboratory().getLaboratoryAccredited())
                                .build();
                            calculationAnalysisMethods.add(bfAnalysis);
                        }
                    );
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