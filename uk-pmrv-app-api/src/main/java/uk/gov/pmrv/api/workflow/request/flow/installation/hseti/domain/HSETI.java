package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HSETI {

    private HSETIAllocationPeriod allocationPeriod;

    @NotNull
    private UUID hsetiFile;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();


	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String notes;
}
