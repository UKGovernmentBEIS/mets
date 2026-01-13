package uk.gov.pmrv.api.migration.report;

import java.time.Year;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.AerInitiatorRequest;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerDueDateService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRCreationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRDueDateService;

@Component
@WebEndpoint(id = "aerAlrTrigger")
@ConditionalOnAvailableEndpoint(endpoint = AerAlrTriggerMigrationService.class)
@RequiredArgsConstructor
@Log4j2
public class AerAlrTriggerMigrationService {
    private final AerCreationService aerCreationService;
    private final ALRCreationService alrCreationService;
    private final AerDueDateService aerDueDateService;
    private final ALRDueDateService alrDueDateService;

    /**
     *
     * @param workflowArg AER, ALR
     * @param accountIdArg the account ID
     * @param yearArg the year to trigger (i.e. 2025) of FINAL for ALR Final year
     * @param requestTypeArg used only if workflowArg is AER and can have one of the values: AER, PERMIT_SURRENDER or PERMIT_REVOCATION
     * @return results of the operation, either success or failure messages
     */
    @WriteOperation
    @Transactional
    public List<String> triggerAerOrAlr(String workflowArg, String accountIdArg, String yearArg, @Nullable String requestTypeArg) {
        final List<String> results = new ArrayList<>();

        if (!isValidWorkflowArg(workflowArg, results)) {
            return results;
        }

        Long accountId = parseAccountId(accountIdArg, results);
        if (accountId == null) {
            return results;
        }

        if ("AER".equals(workflowArg)) {
            handleAerWorkflow(accountId, yearArg, requestTypeArg, results, accountIdArg);
        } else {
            handleAlrWorkflow(accountId, yearArg, results, accountIdArg);
        }

        return results;
    }

    private boolean isValidWorkflowArg(String workflowArg, List<String> results) {
        if (!"AER".equals(workflowArg) && !"ALR".equals(workflowArg)) {
            results.add("unsupported workflowArg!");
            return false;
        }
        return true;
    }

    private Long parseAccountId(String accountIdArg, List<String> results) {
        try {
            return Long.parseLong(accountIdArg);
        } catch (NumberFormatException e) {
            results.add("wrong accountId format!");
            return null;
        }
    }

    private void handleAerWorkflow(Long accountId, String yearArg, String requestTypeArg, List<String> results, String accountIdArg) {
        Year year = parseYear(yearArg, results);
        if (year == null) {
            return;
        }

        if (requestTypeArg == null) {
            results.add("requestTypeArg is required for AER!");
            return;
        }

        RequestType requestType = parseRequestType(requestTypeArg, results);
        if (requestType == null) {
            return;
        }

        AerInitiatorRequest aerInitiatorRequest = AerInitiatorRequest.builder()
            .type(requestType)
            .build();

        Date dueDate = aerDueDateService.generateDueDate();

        Request request = aerCreationService.createRequestAerForYear(accountId, year, dueDate, aerInitiatorRequest);
        results.add("SUCCESS! Triggered AER with request ID '" + request.getId() + "'");
        log.info("Successfully triggered 'AER' with requestId '{}' for accountId: '{}', year: '{}'", request.getId(), accountIdArg, year);

    }

    private void handleAlrWorkflow(Long accountId, String yearArg, List<String> results, String accountIdArg) {
        boolean isFinalYear = "FINAL".equalsIgnoreCase(yearArg);
        Year year = null;
        if (!isFinalYear) {
            year = parseYear(yearArg, results);
            if (year == null) {
                return;
            }
        }

        Date dueDate = alrDueDateService.generateDueDate();
        alrCreationService.createALRForYear(accountId, isFinalYear ? Year.now() : year, dueDate, isFinalYear);
        results.add("SUCCESS! Triggered ALR" + (isFinalYear ? " FINAL" : " for year " + year));
        log.info("Successfully triggered 'ALR' for accountId: '{}', year: '{}'", accountIdArg, yearArg);
    }

    private Year parseYear(String yearArg, List<String> results) {
        try {
            return Year.of(Integer.parseInt(yearArg));
        } catch (NumberFormatException e) {
            results.add("wrong year format!");
            return null;
        }
    }

    private RequestType parseRequestType(String requestTypeArg, List<String> results) {
        return switch (requestTypeArg) {
            case "AER" -> RequestType.AER;
            case "PERMIT_SURRENDER" -> RequestType.PERMIT_SURRENDER;
            case "PERMIT_REVOCATION" -> RequestType.PERMIT_REVOCATION;
            default -> {
                results.add("unsupported requestType!");
                yield null;
            }
        };
    }

}
