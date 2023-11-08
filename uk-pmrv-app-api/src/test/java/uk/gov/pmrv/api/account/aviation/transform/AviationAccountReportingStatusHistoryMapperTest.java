package uk.gov.pmrv.api.account.aviation.transform;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingStatusHistoryDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;

class AviationAccountReportingStatusHistoryMapperTest {

	private final AviationAccountReportingStatusHistoryMapper cut = Mappers.getMapper(AviationAccountReportingStatusHistoryMapper.class);
	
	@Test
	void toReportingStatusHistoryDTO() {
		AviationAccountReportingStatusHistory reportingStatusHistory = AviationAccountReportingStatusHistory.builder()
				.reason("reason")
				.status(AviationAccountReportingStatus.REQUIRED_TO_REPORT)
				.submissionDate(LocalDateTime.now())
				.submitterName("submitter")
				.build();
		
		AviationAccountReportingStatusHistoryDTO result = cut.toReportingStatusHistoryDTO(reportingStatusHistory);
		
		assertThat(result).isEqualTo(AviationAccountReportingStatusHistoryDTO.builder()
				.reason(reportingStatusHistory.getReason())
				.status(reportingStatusHistory.getStatus())
				.submissionDate(reportingStatusHistory.getSubmissionDate())
				.submitterName(reportingStatusHistory.getSubmitterName())
				.build());
	}
}
