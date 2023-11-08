package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDre {

    @Valid
    @NotNull
    private AviationDreDeterminationReason determinationReason;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal totalReportableEmissions;

    @Valid
    @NotNull
    private AviationDreEmissionsCalculationApproach calculationApproach;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingDocuments = new HashSet<>();

    @Valid
    @NotNull
    private AviationDreFee fee;

}
