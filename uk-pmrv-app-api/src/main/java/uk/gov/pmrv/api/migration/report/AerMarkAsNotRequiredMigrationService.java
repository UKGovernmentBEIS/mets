package uk.gov.pmrv.api.migration.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationMarkNotRequiredRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerMarkNotRequiredDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.mapper.AerMapper;

import java.util.ArrayList;
import java.util.List;

@Component
@WebEndpoint(id = "aer-mark-as-not-required")
@ConditionalOnAvailableEndpoint(endpoint = AerMarkAsNotRequiredMigrationService.class)
@RequiredArgsConstructor
@Log4j2
public class AerMarkAsNotRequiredMigrationService {
    private static final String DELETE_REASON = "Old AER marked as not required (METS-627)";

    private final RequestRepository requestRepository;
    private final WorkflowService workflowService;
    private final RequestService requestService;
    private static final AerMapper aerMapper = Mappers.getMapper(AerMapper.class);

    @WriteOperation
    @Transactional
    public List<String> markAerNotRequired(String id, String year, String reason, String userId) {
        final List<String> results = new ArrayList<>();
        long accountId;
        int yearInteger;
        try {
            accountId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            results.add("wrong accountId format!");
            return results;
        }
        try {
            yearInteger = Integer.parseInt(year);
        } catch (NumberFormatException e) {
            results.add("wrong year format!");
            return results;
        }

        List<Request> requests =
            requestRepository.findAllByAccountIdAndTypeInAndMetadataYearAndStatusInOrderByEndDateDesc(
                accountId,
                List.of(RequestType.AER.toString()),
                yearInteger,
                List.of(RequestStatus.IN_PROGRESS.toString())
            );

        
        if (requests.isEmpty()) {
            results.add("No AER request for accountID '" + id + "' and year '" + year + "' found");
        } else if (requests.size() > 1) {
            results.add("More than one AER requests for accountID '" + id + "' and year '" + year + "' found");
        } else {
            AerMarkNotRequiredDetails aerMarkNotRequiredDetails = new AerMarkNotRequiredDetails();
            aerMarkNotRequiredDetails.setReason(reason);

            Request request = requestService.findRequestById(requests.getFirst().getId());

            AerApplicationMarkNotRequiredRequestActionPayload requestActionPayload = aerMapper
                .toAerApplicationMarkNotRequiredRequestActionPayload(aerMarkNotRequiredDetails);

            request.setStatus(RequestStatus.NOT_REQUIRED);
            requestService.addActionToRequest(
                request,
                requestActionPayload,
                RequestActionType.AER_APPLICATION_NOT_REQUIRED,
                userId
            );
            workflowService.deleteProcessInstance(request.getProcessInstanceId(), DELETE_REASON);
            results.add("SUCCESS! Marked as not required AER with request ID '" + request.getId() + "'");
            log.info("Successfully marked AER with requestId '{}' as not required for accountId: '{}', year: '{}', by user with id:'{}' (METS-627)",
                request.getId(), id, year, userId);
        }
        return results;
    }

}
