package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

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
import uk.gov.pmrv.api.permit.domain.common.ProcedureForm;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.PermitMonitoringApproachSection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
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
public class CalculationOfPFCMonitoringApproach extends PermitMonitoringApproachSection {

    @NotBlank
    @Size(max = 30000)
    private String approachDescription;

    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<CellAndAnodeType> cellAndAnodeTypes = new ArrayList<>();

    @Valid
    @NotNull
    private ProcedureForm collectionEfficiency;

    @Valid
    @NotNull
    private PFCTier2EmissionFactor tier2EmissionFactor;
    
    @Valid
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<PFCSourceStreamCategoryAppliedTier> sourceStreamCategoryAppliedTiers = new ArrayList<>();
    
    @Override
    public Set<UUID> getAttachmentIds(){
        return this.streamAttachments().flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public void clearAttachmentIds() {
        this.streamAttachments().forEach(Set::clear);
    }

    private Stream<Set<UUID>> streamAttachments() {

        return Stream.concat(
            sourceStreamCategoryAppliedTiers.stream()
                .map(PFCSourceStreamCategoryAppliedTier::getActivityData)
                .filter(Objects::nonNull)
                .map(PFCActivityData::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files)),

            sourceStreamCategoryAppliedTiers.stream()
                .map(PFCSourceStreamCategoryAppliedTier::getEmissionFactor)
                .filter(Objects::nonNull)
                .map(PFCEmissionFactor::getHighestRequiredTier)
                .filter(Objects::nonNull)
                .map(HighestRequiredTier::getNoHighestRequiredTierJustification)
                .filter(Objects::nonNull)
                .map(NoHighestRequiredTierJustification::getFiles)
                .filter(files -> !ObjectUtils.isEmpty(files))
        );
    }
}
