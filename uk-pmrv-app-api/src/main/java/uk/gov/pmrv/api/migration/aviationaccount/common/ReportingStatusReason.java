package uk.gov.pmrv.api.migration.aviationaccount.common;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportingStatusReason {
	
	@Size(max = 2000)
    private String reason;
}
