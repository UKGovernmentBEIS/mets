package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.EmpUkEtsOriginatedData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.submit.domain.AviationAerUkEtsApplicationSubmitRequestTaskPayload;

class AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsServiceTest {

	@Test
	void sync_upon_reporting_true() {
		Boolean reportingRequired = Boolean.TRUE;
		UUID empOriginatedDoc1Uuid = UUID.randomUUID();
		UUID empOriginatedDoc2Uuid = UUID.randomUUID();
		UUID reportingObligationSupportingDoc = UUID.randomUUID();
		AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload
				.builder()
				.reportingRequired(Boolean.FALSE)
				.reportingObligationDetails(AviationAerReportingObligationDetails.builder()
						.supportingDocuments(Set.of(reportingObligationSupportingDoc))
						.build())
				.aerAttachments(new HashMap<>() {
					{
						put(reportingObligationSupportingDoc, "suppDoc");
					}
				})
				.empOriginatedData(EmpUkEtsOriginatedData.builder().operatorDetailsAttachments(new HashMap<>() {
					{
						put(empOriginatedDoc1Uuid, "fileName1");
						put(empOriginatedDoc2Uuid, "fileName2");
					}
				}).build()).build();
		
		new AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService().sync(reportingRequired, requestTaskPayload);
		
		assertThat(requestTaskPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(
				empOriginatedDoc1Uuid, "fileName1",
				empOriginatedDoc2Uuid, "fileName2"
				));
	}
	
	@Test
	void sync_do_nothing() {
		Boolean reportingRequired = Boolean.TRUE;
		UUID file1Uuid = UUID.randomUUID();
		AviationAerUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload = AviationAerUkEtsApplicationSubmitRequestTaskPayload
				.builder()
				.reportingRequired(Boolean.TRUE)
				.empOriginatedData(EmpUkEtsOriginatedData.builder().operatorDetailsAttachments(new HashMap<>() {
					{
						put(file1Uuid, "fileName1");
						put(UUID.randomUUID(), "fileName2");
					}
				}).build())
				.aerAttachments(new HashMap<>() {
					{
						put(file1Uuid, "fileName1");
					}
				})
				.build();
		
		new AviationAerUkEtsSubmitRequestTaskSyncAerAttachmentsService().sync(reportingRequired, requestTaskPayload);
		
		assertThat(requestTaskPayload.getAerAttachments()).containsEntry(file1Uuid, "fileName1");
	}
}
