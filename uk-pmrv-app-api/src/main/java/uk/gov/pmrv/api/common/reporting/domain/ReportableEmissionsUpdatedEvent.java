package uk.gov.pmrv.api.common.reporting.domain;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Year;

@Data
@SuperBuilder
public abstract class ReportableEmissionsUpdatedEvent {

	private Long accountId;
	private Year year;
	private BigDecimal reportableEmissions;
	private boolean isFromDre;
	private boolean isFromRegulator;
}
