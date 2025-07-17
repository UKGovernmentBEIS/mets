package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OperatorAirImprovementYesResponse extends OperatorAirImprovementResponse {

    @NotNull
    @Size(max = 10000)
    private String proposal;

    @NotNull
    private LocalDate proposedDate;
    
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @JsonIgnore
    @Override
    public Set<UUID> getAirFiles() {
        return this.files;
    }
}
