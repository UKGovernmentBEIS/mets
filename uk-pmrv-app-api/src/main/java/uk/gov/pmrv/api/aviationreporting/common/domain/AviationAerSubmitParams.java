package uk.gov.pmrv.api.aviationreporting.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AviationAerSubmitParams {

    private Long accountId;
    private AviationAerContainer aerContainer;
}
