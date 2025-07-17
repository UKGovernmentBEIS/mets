package uk.gov.pmrv.api.workflow.request.flow.common.vir.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#isAddressed) == (#addressedDate != null)", message = "operatorImprovementResponse.isAddressed")
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#uploadEvidence) == (#files?.size() gt 0)", message = "operatorImprovementResponse.uploadEvidence")
public class OperatorImprovementResponse {

    @NotNull
    private Boolean isAddressed;

    @NotBlank
    @Size(max = 10000)
    private String addressedDescription;

    private LocalDate addressedDate;

    @NotNull
    private Boolean uploadEvidence;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}
