package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class GrantDetermination extends Determination {
    
	@NotNull
    private LocalDate activationDate;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SortedMap<String, @Digits(integer = 8, fraction = 1) @Positive BigDecimal> annualEmissionsTargets = new TreeMap<>();
}
