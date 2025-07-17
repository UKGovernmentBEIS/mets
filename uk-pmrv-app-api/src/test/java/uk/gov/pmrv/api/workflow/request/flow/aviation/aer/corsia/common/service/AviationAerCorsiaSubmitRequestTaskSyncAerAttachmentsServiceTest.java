package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerReportingObligationDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.EmpCorsiaOriginatedData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

class AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsServiceTest {

	@Test
	void sync_upon_reporting_true() {
		Boolean reportingRequired = Boolean.TRUE;
		UUID empOriginatedDoc1Uuid = UUID.randomUUID();
		UUID empOriginatedDoc2Uuid = UUID.randomUUID();
		UUID reportingObligationSupportingDoc = UUID.randomUUID();
		AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload
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
				.empOriginatedData(EmpCorsiaOriginatedData.builder().operatorDetailsAttachments(new HashMap<>() {
					{
						put(empOriginatedDoc1Uuid, "fileName1");
						put(empOriginatedDoc2Uuid, "fileName2");
					}
				}).build()).build();
		
		new AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService().sync(reportingRequired, requestTaskPayload);
		
		assertThat(requestTaskPayload.getAerAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(
				empOriginatedDoc1Uuid, "fileName1",
				empOriginatedDoc2Uuid, "fileName2"
				));
	}
	
	@Test
	void sync_do_nothing() {
		Boolean reportingRequired = Boolean.TRUE;
		UUID file1Uuid = UUID.randomUUID();
		AviationAerCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload = AviationAerCorsiaApplicationSubmitRequestTaskPayload
				.builder()
				.reportingRequired(Boolean.TRUE)
				.empOriginatedData(EmpCorsiaOriginatedData.builder().operatorDetailsAttachments(new HashMap<>() {
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
		
		new AviationAerCorsiaSubmitRequestTaskSyncAerAttachmentsService().sync(reportingRequired, requestTaskPayload);
		
		assertThat(requestTaskPayload.getAerAttachments()).containsEntry(file1Uuid, "fileName1");
	}
}
