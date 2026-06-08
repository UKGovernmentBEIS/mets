package uk.gov.pmrv.api.web.orchestrator.account.aviation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountReportingObligationFirstYearDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountUpdateService;
import uk.gov.pmrv.api.account.domain.enumeration.AccountDetailsHistoryCategory;
import uk.gov.pmrv.api.account.service.AccountDetailsHistoryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme.UK_ETS_AVIATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class AviationAccountEmpCommandOrchestrator {

    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final AviationAccountUpdateService aviationAccountUpdateService;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final ApplicationEventPublisher publisher;
    private final RequestQueryService requestService;
    private final AviationAccountReportingStatusQueryOrchestrator aviationAccountReportingStatusQueryOrchestrator;
    private final AccountDetailsHistoryService accountDetailsHistoryService;
    private final AviationAerCreationService aviationAerCreationService;


    @Transactional
    public void updateAccountFirstYearOfReportingObligation(Long accountId ,
                                                            AviationAccountReportingObligationFirstYearDTO reportingObligationFirstYearDTO,
                                                            AppUser user) {

        AviationAccountDTO aviationAccountDTO = aviationAccountQueryService.getAviationAccountDTOById(accountId);
        validateCommencementDateDTO(aviationAccountDTO, reportingObligationFirstYearDTO);
        LocalDate currentReportingDate = aviationAccountDTO.getCommencementDate();
        aviationAccountUpdateService.updateAccountCommencementDate(accountId,reportingObligationFirstYearDTO.getCommencementDate());

        if (UK_ETS_AVIATION.equals(aviationAccountDTO.getEmissionTradingScheme())) {
            sendAccountUpdateToRegistry(aviationAccountDTO);
        }

        updateReportingStatusRows(reportingObligationFirstYearDTO.getCommencementDate(),currentReportingDate,accountId,aviationAccountDTO.getEmissionTradingScheme());

        accountDetailsHistoryService.createAccountDetailsHistory(accountId,
                AccountDetailsHistoryCategory.FIRST_YEAR_OF_REPORTING_OBLIGATION,currentReportingDate,
                reportingObligationFirstYearDTO.getCommencementDate(), reportingObligationFirstYearDTO.getReason(),
                user);
    }


    private void validateCommencementDateDTO(AviationAccountDTO account, AviationAccountReportingObligationFirstYearDTO commencementDateDTO) {
        int year = commencementDateDTO.getCommencementDate().getYear();
        int currentYear = LocalDate.now().getYear();

        if (year < 2021 || year > currentYear) {
            throw new BusinessException(
                    MetsErrorCode.AVIATION_COMMENCEMENT_DATE_NOT_BEFORE_2021_NOT_AFTER_CURRENT_YEAR,
                    commencementDateDTO,
                    account.getCompetentAuthority(),
                    account.getEmissionTradingScheme()
            );
        }
    }

    private void sendAccountUpdateToRegistry(AviationAccountDTO aviationAccountDTO) {
        Optional<EmissionsMonitoringPlanUkEtsDTO> empOptional = emissionsMonitoringPlanQueryService.getEmissionsMonitoringPlanUkEtsDTOByAccountId(aviationAccountDTO.getId());
        AviationAccountUpdatedRegistryEvent aviationAccountUpdatedRegistryEvent = AviationAccountUpdatedRegistryEvent
                .builder().accountId(aviationAccountDTO.getId()).skipRequestAction(true).build();
        if (empOptional.isPresent()) {
            aviationAccountUpdatedRegistryEvent.setEmissionsMonitoringPlan(empOptional.get().getEmpContainer().getEmissionsMonitoringPlan());
        }
        else {
            aviationAccountUpdatedRegistryEvent.setEmissionsMonitoringPlan(getEmpFromPendingApprovalRequest(aviationAccountDTO.getId()));
        }

        publisher.publishEvent(aviationAccountUpdatedRegistryEvent);
    }

    private void updateReportingStatusRows(LocalDate newReportingDate , LocalDate previousReportingDate, Long accountId,
                                           EmissionTradingScheme emissionTradingScheme) {
        int yearDifference = newReportingDate.getYear() - previousReportingDate.getYear();
        if(yearDifference < 0) {
            List<Integer> yearsToAdd = new ArrayList<>();
            for (int i = 1 ; i <= Math.abs(yearDifference); i++) {
                yearsToAdd.add(previousReportingDate.getYear()-i);
            }
            aviationAccountReportingStatusQueryOrchestrator.addReportingStatusesForYears(yearsToAdd, accountId);
            // create AERs for the years added
            yearsToAdd.forEach(y-> {
                try {
                    aviationAerCreationService.createAerFromFirstYearOfReportingObligation(accountId, Year.of(y),emissionTradingScheme);
                } catch (BusinessException ex) {
                    log.info("Did not create AER for account {} and year {}: {}", accountId, y, ex.getMessage());
                }
            });
        }
    }


    public EmissionsMonitoringPlanUkEts getEmpFromPendingApprovalRequest(Long accountId) {
        List<Request> requestList =
                requestService.findRequestsByAccountIdAndType(accountId, RequestType.EMP_ISSUANCE_UKETS);
        Optional<RequestTask> requestTask = requestList.stream()
                .findFirst()
                .map(Request::getRequestTasks)
                .orElse(Collections.emptyList())
                .stream()
                .filter(rt -> RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_REVIEW.equals(rt.getType()))
                .findFirst();
        return requestTask.map(task -> ((EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) task.getPayload()).getEmissionsMonitoringPlan()).orElse(null);
    }


}
