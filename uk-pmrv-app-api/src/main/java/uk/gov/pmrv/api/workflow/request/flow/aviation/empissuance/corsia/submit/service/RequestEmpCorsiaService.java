package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.mapper.EmpCorsiaSubmitMapper;

@Service
@RequiredArgsConstructor
public class RequestEmpCorsiaService {

    private final RequestService requestService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanCorsiaContainer> empCorsiaValidatorService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpCorsiaSubmitMapper EMP_CORSIA_SUBMIT_MAPPER = Mappers.getMapper(EmpCorsiaSubmitMapper.class);

    @Transactional
    public void applySaveAction(EmpIssuanceCorsiaSaveApplicationRequestTaskActionPayload requestTaskActionPayload,
                                RequestTask requestTask) {
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload requestTaskPayload =
            (EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        requestTaskPayload.setEmissionsMonitoringPlan(requestTaskActionPayload.getEmissionsMonitoringPlan());
        requestTaskPayload.setEmpSectionsCompleted(requestTaskActionPayload.getEmpSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(RequestTask requestTask, AppUser appUser) {
        EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload empIssuanceApplicationSubmitRequestTaskPayload =
            (EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        EmissionsMonitoringPlanCorsiaContainer empCorsiaContainer =
            EMP_CORSIA_SUBMIT_MAPPER.toEmissionsMonitoringPlanCorsiaContainer(empIssuanceApplicationSubmitRequestTaskPayload);

        empCorsiaValidatorService.validateEmissionsMonitoringPlan(empCorsiaContainer);

        //update request payload
        Request request = requestTask.getRequest();
        EmpIssuanceCorsiaRequestPayload empIssuanceCorsiaRequestPayload = (EmpIssuanceCorsiaRequestPayload) request.getPayload();
        empIssuanceCorsiaRequestPayload.setEmissionsMonitoringPlan(empIssuanceApplicationSubmitRequestTaskPayload.getEmissionsMonitoringPlan());
        empIssuanceCorsiaRequestPayload.setEmpAttachments(empIssuanceApplicationSubmitRequestTaskPayload.getEmpAttachments());
        empIssuanceCorsiaRequestPayload.setEmpSectionsCompleted(empIssuanceApplicationSubmitRequestTaskPayload.getEmpSectionsCompleted());

        //add request action for emp submission
        addEmpApplicationSubmittedRequestAction(empIssuanceApplicationSubmitRequestTaskPayload, request, appUser);
    }

    private void addEmpApplicationSubmittedRequestAction(EmpIssuanceCorsiaApplicationSubmitRequestTaskPayload empIssuanceApplicationSubmitRequestTaskPayload,
                                                         Request request, AppUser appUser) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload empApplicationSubmittedPayload =
            EMP_CORSIA_SUBMIT_MAPPER.toEmpIssuanceCorsiaApplicationSubmittedRequestActionPayload(empIssuanceApplicationSubmitRequestTaskPayload, accountInfo);

        requestService.addActionToRequest(request, empApplicationSubmittedPayload, RequestActionType.EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED, appUser.getUserId());
    }
}
