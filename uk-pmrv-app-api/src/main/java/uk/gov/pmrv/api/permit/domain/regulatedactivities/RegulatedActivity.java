package uk.gov.pmrv.api.permit.domain.regulatedactivities;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.permit.domain.PermitIdSection;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RegulatedActivity extends PermitIdSection {

    @NotNull
    private RegulatedActivityType type;
    
    @Positive
    private BigDecimal capacity;

    @NotNull
    private CapacityUnit capacityUnit;
}
