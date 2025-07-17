package uk.gov.pmrv.api.reporting.domain.nace;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NaceCodes {
    @NotEmpty
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<NaceCode> codes = new HashSet<>();
}
