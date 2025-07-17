package uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.ukets;

import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.templateparams.TemplateParams;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpCreateDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.emp.EmpPreviewCreateEmpDocumentService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.notification.DocumentTemplateEmpParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.common.service.EmpIssuanceRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@Service
public class EmpUkEtsPreviewCreateEmpDocumentService extends
        EmpPreviewCreateEmpDocumentService<EmissionsMonitoringPlanUkEtsContainer> {


    private final EmissionsMonitoringPlanQueryService empQueryService;
    private final EmpCreateDocumentService empCreateDocumentService;
    private final EmpIssuanceRequestIdGenerator generator;

    public EmpUkEtsPreviewCreateEmpDocumentService(DocumentTemplateEmpParamsProvider empParamsProvider,
                                                   EmissionsMonitoringPlanQueryService empQueryService,
                                                   EmpCreateDocumentService empCreateDocumentService,
                                                   EmpIssuanceRequestIdGenerator generator) {
        super(empParamsProvider);
        this.empQueryService = empQueryService;
        this.empCreateDocumentService = empCreateDocumentService;
        this.generator = generator;
    }

    public FileDTO getFile(final DecisionNotification decisionNotification,
                           final Request request,
                           final Long accountId,
                           final EmissionsMonitoringPlanUkEts emp,
                           final ServiceContactDetails serviceContactDetails,
                           final Map<UUID, String> attachments,
                           final int consolidationNumber) {

        final String signatory = decisionNotification.getSignatory();

        final EmissionsMonitoringPlanUkEtsDTO empDto = this.createEmpDto(accountId, emp, serviceContactDetails,
                attachments, consolidationNumber);

        final TemplateParams empParams = this.constructTemplateParams(request, signatory, empDto, consolidationNumber);

        return empCreateDocumentService.generateDocumentWithParams(empDto, DocumentTemplateType.EMP_UKETS, empParams);
    }

    private EmissionsMonitoringPlanUkEtsDTO createEmpDto(final Long accountId,
                                                         final EmissionsMonitoringPlanUkEts emp,
                                                         final ServiceContactDetails serviceContactDetails,
                                                         final Map<UUID, String> attachments,
                                                         final int newConsolidationNumber) {

        // in case of variation the emp id exists, otherwise it has to be generated
        final String empId = empQueryService.getEmpIdByAccountId(accountId).orElse(
            generator.generate(RequestParams.builder().accountId(accountId).build())
        );
        
        return EmissionsMonitoringPlanUkEtsDTO.builder()
            .id(empId)
            .empContainer(
                EmissionsMonitoringPlanUkEtsContainer.builder()
                    .emissionsMonitoringPlan(emp)
                    .serviceContactDetails(serviceContactDetails)
                    .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                    .empAttachments(attachments)
                    .build())
            .consolidationNumber(newConsolidationNumber)
            .accountId(accountId)
            .build();
    }
}
