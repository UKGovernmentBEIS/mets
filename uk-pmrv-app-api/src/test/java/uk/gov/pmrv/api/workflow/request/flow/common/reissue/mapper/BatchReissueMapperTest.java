package uk.gov.pmrv.api.workflow.request.flow.common.reissue.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueFilters;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitBatchReissueRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitreissue.domain.PermitReissueAccountReport;

class BatchReissueMapperTest {

	private final BatchReissueMapper cut = Mappers.getMapper(BatchReissueMapper.class);

    @Test
    void toSubmittedActionPayload() {
    	PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.installationCategories(Set.of(InstallationCategory.A))
				.emitterTypes(Set.of(EmitterType.GHGE))
				.build();
    	BatchReissueRequestPayload requestPayload = BatchReissueRequestPayload.builder()
    			.filters(filters)
    			
    			.signatory("signatory")
    			.build();
    	
    	PermitBatchReissueRequestMetadata metadata = PermitBatchReissueRequestMetadata.builder()
    			.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
    			.submitter("submitter")
    			.build();
    	
    	String signatoryName = "signName";
    	
    	BatchReissueSubmittedRequestActionPayload result = cut.toSubmittedActionPayload(requestPayload, metadata, signatoryName, RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD);
    	
    	assertThat(result).isEqualTo(BatchReissueSubmittedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD)
    			.filters(filters)
    			.submitter("submitter")
    			.signatory("signatory")
    			.signatoryName(signatoryName)
    			.build());
    }
    
    @Test
    void toCompletedActionPayload() {
    	PermitBatchReissueFilters filters = PermitBatchReissueFilters.builder()
				.accountStatuses(Set.of(InstallationAccountStatus.LIVE))
				.installationCategories(Set.of(InstallationCategory.A))
				.emitterTypes(Set.of(EmitterType.GHGE))
				.build();
    	BatchReissueRequestPayload requestPayload = BatchReissueRequestPayload.builder()
    			.filters(filters)
    			.signatory("signatory")
    			.build();
    	PermitBatchReissueRequestMetadata metadata = PermitBatchReissueRequestMetadata.builder()
    			.type(RequestMetadataType.PERMIT_BATCH_REISSUE)
    			.submitter("submitter")
    			.accountsReports(Map.of(
    					1L, PermitReissueAccountReport.builder().installationName("inst1").build(),
    					2L, PermitReissueAccountReport.builder().installationName("inst2").build()
    					))
    			.build();  
    	String signatoryName = "signName";
    	
    	BatchReissueCompletedRequestActionPayload result = cut.toCompletedActionPayload(requestPayload, metadata, signatoryName, RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD);
    	
    	assertThat(result).isEqualTo(BatchReissueCompletedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD)
    			.filters(filters)
    			.submitter("submitter")
    			.signatory("signatory")
    			.signatoryName(signatoryName)
    			.numberOfAccounts(2)
    			.build());
    }
}
