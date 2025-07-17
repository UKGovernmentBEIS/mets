package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PermanentCessation {

    @NotBlank
    @Size(max=10000)
    private String description;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @NotNull
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate cessationDate;

    @NotNull
    private PermanentCessationScope cessationScope;

    @NotBlank
    @Size(max=10000)
    private String additionalDetails;

    @Size(max=10000)
    private String regulatorComments;

}
