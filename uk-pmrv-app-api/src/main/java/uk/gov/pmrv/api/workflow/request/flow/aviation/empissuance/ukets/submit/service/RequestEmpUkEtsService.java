package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.validation.EmpTradingSchemeValidatorService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.domain.RequestAviationAccountInfo;
import uk.gov.pmrv.api.workflow.request.flow.aviation.common.service.RequestAviationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.mapper.EmpUkEtsSubmitMapper;

@Service
@RequiredArgsConstructor
public class RequestEmpUkEtsService {
    private final RequestService requestService;
    private final EmpTradingSchemeValidatorService<EmissionsMonitoringPlanUkEtsContainer> empUkEtsValidatorService;
    private final RequestAviationAccountQueryService requestAviationAccountQueryService;
    private static final EmpUkEtsSubmitMapper EMP_UK_ETS_SUBMIT_MAPPER = Mappers.getMapper(EmpUkEtsSubmitMapper.class);

    @Transactional
    public void applySaveAction(EmpIssuanceUkEtsSaveApplicationRequestTaskActionPayload requestTaskActionPayload,
                                RequestTask requestTask) {
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload requestTaskPayload =
            (EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setEmissionsMonitoringPlan(requestTaskActionPayload.getEmissionsMonitoringPlan());
        requestTaskPayload.setEmpSectionsCompleted(requestTaskActionPayload.getEmpSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(RequestTask requestTask, PmrvUser pmrvUser) {
        //validate emp
        EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload empIssuanceApplicationSubmitRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        EmissionsMonitoringPlanUkEtsContainer empUkEtsContainer =
            EMP_UK_ETS_SUBMIT_MAPPER.toEmissionsMonitoringPlanUkEtsContainer(empIssuanceApplicationSubmitRequestTaskPayload);

        empUkEtsValidatorService.validateEmissionsMonitoringPlan(empUkEtsContainer);

        //update request payload
        Request request = requestTask.getRequest();
        EmpIssuanceUkEtsRequestPayload empIssuanceUkEtsRequestPayload = (EmpIssuanceUkEtsRequestPayload) request.getPayload();
        empIssuanceUkEtsRequestPayload.setEmissionsMonitoringPlan(empIssuanceApplicationSubmitRequestTaskPayload.getEmissionsMonitoringPlan());
        empIssuanceUkEtsRequestPayload.setEmpAttachments(empIssuanceApplicationSubmitRequestTaskPayload.getEmpAttachments());
        empIssuanceUkEtsRequestPayload.setEmpSectionsCompleted(empIssuanceApplicationSubmitRequestTaskPayload.getEmpSectionsCompleted());

        //add request action for emp submission
        addEmpApplicationSubmittedRequestAction(empIssuanceApplicationSubmitRequestTaskPayload, request, pmrvUser);
    }

    private void addEmpApplicationSubmittedRequestAction(EmpIssuanceUkEtsApplicationSubmitRequestTaskPayload empIssuanceApplicationSubmitRequestTaskPayload,
                                                         Request request, PmrvUser pmrvUser) {
        RequestAviationAccountInfo accountInfo = requestAviationAccountQueryService.getAccountInfo(request.getAccountId());

        EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload empApplicationSubmittedPayload =
            EMP_UK_ETS_SUBMIT_MAPPER.toEmpIssuanceUkEtsApplicationSubmittedRequestActionPayload(empIssuanceApplicationSubmitRequestTaskPayload, accountInfo);

        requestService.addActionToRequest(request, empApplicationSubmittedPayload, RequestActionType.EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED, pmrvUser.getUserId());
    }
}
