package uk.gov.pmrv.api.account.aviation.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAccountReportingExemptEvent {

    private Long accountId;
    private String submitterId;
    private Year year;
}
