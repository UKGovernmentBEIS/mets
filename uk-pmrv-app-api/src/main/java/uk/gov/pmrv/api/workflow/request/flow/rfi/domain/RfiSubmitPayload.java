package uk.gov.pmrv.api.workflow.request.flow.rfi.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RfiSubmitPayload {

    @JsonUnwrapped
    @Valid
    @NotNull
    private RfiQuestionPayload rfiQuestionPayload;

    @NotNull
    @Future
    private LocalDate deadline;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<String> operators = new HashSet<>();

    @NotBlank
    private String signatory;
}
