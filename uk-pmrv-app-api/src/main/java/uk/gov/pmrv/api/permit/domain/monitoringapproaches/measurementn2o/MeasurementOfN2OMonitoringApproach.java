package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.common.ProcedureOptionalForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSectionWithTransfer;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@SpELExpression(expression = "{(#hasTransfer && #emissionPointCategoryAppliedTiers.?[#this.emissionPointCategory.transfer == null].empty) " +
    "|| (#hasTransfer == false && #emissionPointCategoryAppliedTiers.?[#this.emissionPointCategory.transfer != null].empty)}",
    message = "permit.monitoringapproach.transfer.exist")
public class MeasurementOfN2OMonitoringApproach extends PermitMonitoringApproachSectionWithTransfer {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;

    @Valid
    @NotNull
    private ProcedureForm emissionDetermination;

    @Valid
    @NotNull
    private ProcedureForm referenceDetermination;

    @Valid
    @NotNull
    private ProcedureForm operationalManagement;

    @Valid
    @NotNull
    private ProcedureForm nitrousOxideEmissionsDetermination;

    @Valid
    @NotNull
    private ProcedureForm nitrousOxideConcentrationDetermination;

    @Valid
    @NotNull
    private ProcedureForm quantityProductDetermination;

    @Valid
    @NotNull
    private ProcedureForm quantityMaterials;

    @Valid
    @NotNull
    private ProcedureOptionalForm gasFlowCalculation;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<MeasurementOfN2OEmissionPointCategoryAppliedTier> emissionPointCategoryAppliedTiers = new ArrayList<>();

    @Override
    public Set<UUID> getAttachmentIds() {
        return this.streamAttachments().flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public void clearAttachmentIds() {
        this.streamAttachments().forEach(Set::clear);
    }

    private Stream<Set<UUID>> streamAttachments() {

        return emissionPointCategoryAppliedTiers.stream()
            .map(MeasurementOfN2OEmissionPointCategoryAppliedTier::getMeasuredEmissions)
            .filter(Objects::nonNull)
            .map(MeasuredEmissions::getHighestRequiredTier)
            .filter(Objects::nonNull)
            .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
            .filter(Objects::nonNull)
            .map(NoHighestRequiredTierJustification::getFiles)
            .filter(files -> !ObjectUtils.isEmpty(files));
    }

    @JsonIgnore
    public String getEmissionPoint() {
        return emissionPointCategoryAppliedTiers.stream()
            .map(MeasurementOfN2OEmissionPointCategoryAppliedTier::getEmissionPointCategory)
            .filter(Objects::nonNull)
            .map(MeasurementOfN2OEmissionPointCategory::getEmissionPoint)
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
