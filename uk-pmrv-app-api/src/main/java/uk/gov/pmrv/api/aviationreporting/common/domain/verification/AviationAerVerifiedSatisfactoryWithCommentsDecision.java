package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerVerifiedSatisfactoryWithCommentsDecision extends AviationAerVerificationDecision {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @NotEmpty
    private List<@NotBlank @Size(max = 10000) String> reasons = new ArrayList<>();
}
