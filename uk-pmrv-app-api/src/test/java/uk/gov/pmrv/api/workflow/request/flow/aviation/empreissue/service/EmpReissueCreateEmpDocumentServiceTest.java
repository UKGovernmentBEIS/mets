package uk.gov.pmrv.api.workflow.request.flow.aviation.empreissue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.abbreviations.EmpAbbreviations;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueRequestMetadata;

@ExtendWith(MockitoExtension.class)
class EmpReissueCreateEmpDocumentServiceTest {

	@InjectMocks
	private EmpReissueCreateEmpDocumentService cut;

	@Mock
	private AccountQueryService accountQueryService;
	
	@Mock
	private EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
	
	@Mock
	private EmpCreateDocumentService empCreateDocumentService;
	
	@Test
	void create() throws InterruptedException, ExecutionException {
		Long accountId = 1L;
		
		ReissueRequestMetadata metadata = ReissueRequestMetadata.builder()
    			.signatory("signatory")
    			.build();
		
		Request request = Request.builder()
				.accountId(accountId)
				.metadata(metadata)
				.build();
		
		EmissionTradingScheme accountEmissionTradingScheme = EmissionTradingScheme.UK_ETS_AVIATION;
		
		EmissionsMonitoringPlanUkEtsDTO emp = EmissionsMonitoringPlanUkEtsDTO.builder()
				.accountId(accountId)
				.empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
						.emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().abbreviations(EmpAbbreviations.builder().exist(true).build()).build())
						.build())
				.build();
		
		final FileInfoDTO document = FileInfoDTO.builder().uuid("uuid").build();
		
		when(accountQueryService.getAccountEmissionTradingScheme(accountId)).thenReturn(accountEmissionTradingScheme);
		when(emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)).thenReturn(Optional.of(emp));
		when(empCreateDocumentService.generateDocumentAsync(request, "signatory", emp, DocumentTemplateType.EMP_UKETS))
        .thenReturn(CompletableFuture.completedFuture(document));
		
		CompletableFuture<FileInfoDTO> result = cut.create(request);
		assertThat(result.get()).isEqualTo(document);
		
		verify(accountQueryService, times(1)).getAccountEmissionTradingScheme(accountId);
        verify(emissionsMonitoringPlanQueryService, times(1)).getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId);
        verify(empCreateDocumentService, times(1)).generateDocumentAsync(request, "signatory", emp, DocumentTemplateType.EMP_UKETS);
		
	}
}
