package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSectionWithTransfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{(#hasTransfer && #sourceStreamCategoryAppliedTiers.?[#this.sourceStreamCategory.transfer == null].empty) " +
    "|| (#hasTransfer == false && #sourceStreamCategoryAppliedTiers.?[#this.sourceStreamCategory.transfer != null].empty)}",
    message = "permit.monitoringapproach.transfer.exist")
public class CalculationOfCO2MonitoringApproach extends PermitMonitoringApproachSectionWithTransfer {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;
    
    @Valid
    @NotNull
    private SamplingPlan samplingPlan;
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<CalculationSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();

    @Override
    public Set<UUID> getAttachmentIds(){
        return this.streamAttachments().flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public void clearAttachmentIds() {
        this.streamAttachments().forEach(Set::clear);
    }

    private Stream<Set<UUID>> streamAttachments() {

        final Stream<Set<UUID>> samplingPlanStream = (
            samplingPlan != null &&
            samplingPlan.getDetails() != null &&
            samplingPlan.getDetails().getProcedurePlan() != null
        ) ? Stream.of(samplingPlan.getDetails().getProcedurePlan().getProcedurePlanIds()) : Stream.of(new HashSet<>());

        return Stream.of(

            samplingPlanStream,

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .map(CalculationEmissionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .map(CalculationEmissionFactor::getOneThirdRuleFiles)
                .filter(Objects::nonNull),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .flatMap(factor -> factor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .flatMap(factor -> factor.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getActivityData)
                .filter(Objects::nonNull)
                .map(CalculationActivityData::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .map(CalculationOxidationFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationOxidationFactor -> calculationOxidationFactor.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getOxidationFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationOxidationFactor -> calculationOxidationFactor.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .map(CalculationCarbonContent::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .map(CalculationCarbonContent::getOneThirdRuleFiles)
                .filter(Objects::nonNull),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .flatMap(carbon -> carbon.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getCarbonContent)
                .filter(Objects::nonNull)
                .flatMap(content -> content.getCalculationAnalysisMethodData().getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .map(CalculationNetCalorificValue::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .flatMap(calculationNetCalorificValue -> calculationNetCalorificValue.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getNetCalorificValue)
                .filter(Objects::nonNull)
                .flatMap(calculationNetCalorificValue -> calculationNetCalorificValue.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .map(CalculationConversionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationConversionFactor -> calculationConversionFactor.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),


            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getConversionFactor)
                .filter(Objects::nonNull)
                .flatMap(calculationConversionFactor -> calculationConversionFactor.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .map(CalculationBiomassFraction::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .flatMap(calculationBiomassFraction -> calculationBiomassFraction.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getReducedSamplingFrequencyJustification)
                .filter(Objects::nonNull)
                .map(ReducedSamplingFrequencyJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(CalculationSourceStreamCategoryAppliedTier::getBiomassFraction)
                .filter(Objects::nonNull)
                .flatMap(calculationBiomassFraction -> calculationBiomassFraction.getCalculationAnalysisMethodData()
                    .getAnalysisMethods().stream())
                .filter(Objects::nonNull)
                .map(CalculationAnalysisMethod::getFiles)
                .filter(Objects::nonNull)
                .filter(files -> !ObjectUtils.isEmpty(files))

        ).flatMap(Function.identity());
    }
}
