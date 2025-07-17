package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.corsia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaDTO;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.notification.template.installation.domain.InstallationAccountTemplateParams;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsSourceData;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaPreviewCreateEmpDocumentServiceTest {
	
	@InjectMocks
    private EmpCorsiaPreviewCreateEmpDocumentService service;

	@Mock
	private DocumentTemplateEmpParamsProvider empParamsProvider;

	@Mock
	private EmissionsMonitoringPlanQueryService empQueryService;

	@Mock
	private EmpCreateDocumentService empCreateDocumentService;
	
	@Mock
	private EmpIssuanceRequestIdGenerator generator;
	
	@Test
	void getFile() {

		final Long accountId = 200L;
		final String signatory = "signatory";
		final DecisionNotification decisionNotification = DecisionNotification.builder().signatory(signatory).build();
		final LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 4, 6, 7);
		final Request request = Request.builder()
			.accountId(accountId)
			.submissionDate(submissionDate)
			.build();
		final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
		final EmissionsMonitoringPlanCorsia emissionsMonitoringPlanCorsia = EmissionsMonitoringPlanCorsia.builder().build();
		final int consolidationNumber = 2;
		final String empId = "empId";
		final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
		final int newConsolidationNumber = consolidationNumber + 1;
		final EmissionsMonitoringPlanCorsiaDTO empDto = EmissionsMonitoringPlanCorsiaDTO.builder()
			.id(empId)
			.accountId(accountId)
			.empContainer(EmissionsMonitoringPlanCorsiaContainer.builder()
				.scheme(EmissionTradingScheme.CORSIA)
				.emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder().build())
				.serviceContactDetails(ServiceContactDetails.builder().build())
				.empAttachments(attachments)
				.build()
			)
			.consolidationNumber(newConsolidationNumber)
			.build();
		final TemplateParams templateParams = TemplateParams.builder()
			.accountParams(InstallationAccountTemplateParams.builder().build())
			.build();
		final DocumentTemplateEmpParamsSourceData sourceData = DocumentTemplateEmpParamsSourceData.builder()
			.request(request)
			.signatory(signatory)
			.empContainer(empDto.getEmpContainer())
			.consolidationNumber(empDto.getConsolidationNumber())
			.build();
		final String fileName = "fileName";
		final FileDTO fileDTO = FileDTO.builder().fileName(fileName).build();

		when(empQueryService.getEmpIdByAccountId(accountId)).thenReturn(Optional.of(empId));
		when(empParamsProvider.constructTemplateParams(sourceData)).thenReturn(templateParams);
		when(empCreateDocumentService.generateDocumentWithParams(empDto, DocumentTemplateType.EMP_CORSIA, templateParams)).thenReturn(
			fileDTO
		);

		final FileDTO result = service.getFile(decisionNotification, request, accountId, emissionsMonitoringPlanCorsia, serviceContactDetails, attachments, newConsolidationNumber);

		assertEquals(result.getFileName(), fileName);
		Assertions.assertNull(templateParams.getAccountParams().getName());
		Assertions.assertNull(templateParams.getAccountParams().getLocation());
	}
}