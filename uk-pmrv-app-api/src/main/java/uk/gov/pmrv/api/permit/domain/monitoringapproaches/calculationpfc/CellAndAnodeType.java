package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

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
public class CellAndAnodeType {

    @NotBlank
    //TODO revisit validation
    @Size(max = 100)
    private String cellType;

    @NotBlank
    //TODO revisit validation
    @Size(max = 100)
    private String anodeType;
}
