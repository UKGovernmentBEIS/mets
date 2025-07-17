package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
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

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmpUkEtsPreviewCreateEmpDocumentServiceTest {


    @InjectMocks
    private EmpUkEtsPreviewCreateEmpDocumentService service;

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
        final Long accountId = 1L;
        final String signatory = "signatory";
        final DecisionNotification decisionNotification = DecisionNotification.builder().signatory(signatory).build();
        final LocalDateTime submissionDate = LocalDateTime.of(2022, 1, 4, 6, 7);
        final Request request = Request.builder()
                .accountId(accountId)
                .submissionDate(submissionDate)
                .build();
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder().build();
        final EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts = EmissionsMonitoringPlanUkEts.builder().build();
        final int consolidationNumber = 2;
        final String empId = "empId";
        final Map<UUID, String> attachments = Map.of(UUID.randomUUID(), "filename");
        final int newConsolidationNumber = consolidationNumber + 1;
        final EmissionsMonitoringPlanUkEtsDTO empDto = EmissionsMonitoringPlanUkEtsDTO.builder()
                .id(empId)
                .accountId(accountId)
                .empContainer(EmissionsMonitoringPlanUkEtsContainer.builder()
                        .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                        .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder().build())
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
        when(empCreateDocumentService.generateDocumentWithParams(empDto, DocumentTemplateType.EMP_UKETS, templateParams)).thenReturn(
                fileDTO
        );

        final FileDTO result = service.getFile(decisionNotification, request, accountId, emissionsMonitoringPlanUkEts, serviceContactDetails, attachments, newConsolidationNumber);

        assertEquals(result.getFileName(), fileName);
        Assertions.assertNull(templateParams.getAccountParams().getName());
        Assertions.assertNull(templateParams.getAccountParams().getLocation());
    }


}
