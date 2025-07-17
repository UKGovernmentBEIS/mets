package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.dto.EmpDetailsDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanService;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.service.ReissueOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class EmpReissueGenerateDocumentsServiceTest {

	@InjectMocks
	private EmpReissueGenerateDocumentsService cut;
	
	@Mock
	private EmpReissueCreateEmpDocumentService empReissueCreateEmpDocumentService;
	
	@Mock
	private ReissueOfficialNoticeService reissueOfficialNoticeService;

	@Mock
	private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Mock
	private EmissionsMonitoringPlanService emissionsMonitoringPlanService;
	
	@Test
	void generateDocuments() {
        final Long accountId = 1L;
        
        final ReissueRequestPayload requestPayload = ReissueRequestPayload.builder()
            .build();
        
    	final Request request = Request.builder()
        		.type(RequestType.EMP_REISSUE)
        		.accountId(accountId)
        		.payload(requestPayload)
        		.build();
    	
    	UUID pdfUuid = UUID.randomUUID();
    	FileInfoDTO empDocument = FileInfoDTO.builder()
    			.name("emp.pdf")
    			.uuid(pdfUuid.toString())
    			.build();
    	
    	UUID officialNoticePdfUuid = UUID.randomUUID();
    	FileInfoDTO officialNotice = FileInfoDTO.builder()
    			.name("offnotice.pdf")
    			.uuid(officialNoticePdfUuid.toString())
    			.build();
    	
    	final EmpDetailsDTO empDetailsDTO = EmpDetailsDTO.builder().id("empId").build();
    	
    	when(empReissueCreateEmpDocumentService.create(request))
    		.thenReturn(CompletableFuture.completedFuture(empDocument));
    	when(reissueOfficialNoticeService.generateOfficialNotice(request))
    		.thenReturn(CompletableFuture.completedFuture(officialNotice));
    	when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId)).thenReturn(Optional.of(empDetailsDTO));
    	
    	cut.generateDocuments(request);
    	
    	verify(empReissueCreateEmpDocumentService, times(1)).create(request);
    	verify(reissueOfficialNoticeService, times(1)).generateOfficialNotice(request);
    	verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanDetailsDTOByAccountId(accountId);
    	verify(emissionsMonitoringPlanService, times(1)).setFileDocumentUuid(empDetailsDTO.getId(), pdfUuid.toString());
    	
    	assertThat(requestPayload.getDocument()).isEqualTo(empDocument);
    	assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
	}
}
