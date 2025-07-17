package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.methodtasks;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MethodTaskConnection {

    @NotNull
    @Size(max = 255)
    private String itemId;

    @NotBlank
    @Size(max = 1000)
    private String itemName;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 2)
    @Builder.Default
    private List<SubInstallationType> subInstallations = new ArrayList<>();

}
