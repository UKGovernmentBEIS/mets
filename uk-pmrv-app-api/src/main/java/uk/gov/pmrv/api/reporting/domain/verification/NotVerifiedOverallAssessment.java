package uk.gov.pmrv.api.reporting.domain.verification;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class NotVerifiedOverallAssessment extends OverallAssessment {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    @NotEmpty
    @Valid
    private List<@NotNull NotVerifiedReason> notVerifiedReasons = new ArrayList<>();
}
