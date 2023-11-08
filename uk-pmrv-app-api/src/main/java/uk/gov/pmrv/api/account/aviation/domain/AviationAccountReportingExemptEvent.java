package uk.gov.pmrv.api.account.aviation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingExemptEvent {

    private Long accountId;
    private String submitterId;
}
