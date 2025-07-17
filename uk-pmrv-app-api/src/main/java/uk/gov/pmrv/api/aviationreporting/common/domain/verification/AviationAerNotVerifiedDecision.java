package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueElements;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerNotVerifiedDecision extends AviationAerVerificationDecision {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    @UniqueElements
    private Set<@NotNull @Valid AviationAerNotVerifiedDecisionReason> notVerifiedReasons = new HashSet<>();
}
