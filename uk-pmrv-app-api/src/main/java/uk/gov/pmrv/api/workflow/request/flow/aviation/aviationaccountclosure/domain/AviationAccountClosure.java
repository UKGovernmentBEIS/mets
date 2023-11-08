package uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountClosure {
	
	@NotBlank
    @Size(max = 10000)
    private String reason;
}
