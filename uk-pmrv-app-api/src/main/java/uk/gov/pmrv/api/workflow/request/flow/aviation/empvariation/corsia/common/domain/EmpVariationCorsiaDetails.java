package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmpVariationCorsiaDetails {

	@NotBlank
    @Size(max=10000)
	private String reason;
	
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NotEmpty
    private List<EmpVariationCorsiaChangeType> changes = new ArrayList<>();
}
