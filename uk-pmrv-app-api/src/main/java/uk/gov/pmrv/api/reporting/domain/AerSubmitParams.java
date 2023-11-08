package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AerSubmitParams {

    private Long accountId;
    private AerContainer aerContainer;
}
