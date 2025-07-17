package uk.gov.pmrv.api.permit.domain.monitoringreporting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringRole {

    @NotBlank
    @Size(max = 10000)
    private String jobTitle;

    @NotBlank
    @Size(max = 10000)
    private String mainDuties;
}
