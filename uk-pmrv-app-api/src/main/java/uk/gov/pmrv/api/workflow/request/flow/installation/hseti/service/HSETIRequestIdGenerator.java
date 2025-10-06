package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestSequenceRequestIdGenerator;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;

import java.util.List;

@Service
public class HSETIRequestIdGenerator extends RequestSequenceRequestIdGenerator {

    private static final String REQUEST_ID_FORMATTER = "%s%05d-%d_%d-%d";
    private static final String SEQUENCE_REQUEST_ID_FORMATTER = "%d-%d_%d";

    public HSETIRequestIdGenerator(RequestSequenceRepository repository) {
        super(repository);
    }

    @Override
    protected RequestSequence resolveRequestSequence(RequestParams params) {
        final Long accountId = params.getAccountId();
        final RequestType type = params.getType();

        HSETIRequestMetadata metaData = (HSETIRequestMetadata) params.getRequestMetadata();

        int yearFrom = getLastTwoDigitsOfYear(metaData.getAllocationPeriod().getPeriodFrom().getValue());
        int yearTo = getLastTwoDigitsOfYear(metaData.getAllocationPeriod().getPeriodTo().getValue());

        final String businessIdentifierKey = String.format(SEQUENCE_REQUEST_ID_FORMATTER, accountId, yearFrom, yearTo);

        return repository.findByBusinessIdentifierAndType(businessIdentifierKey, type)
                .orElse(new RequestSequence(businessIdentifierKey, type));
    }

    @Override
    protected String generateRequestId(Long sequenceNo, RequestParams params) {
        Long accountId = params.getAccountId();
        HSETIRequestMetadata metaData = (HSETIRequestMetadata) params.getRequestMetadata();

        int yearFrom = getLastTwoDigitsOfYear(metaData.getAllocationPeriod().getPeriodFrom().getValue());
        int yearTo = getLastTwoDigitsOfYear(metaData.getAllocationPeriod().getPeriodTo().getValue());

        return String.format(REQUEST_ID_FORMATTER, getPrefix(), accountId, yearFrom, yearTo, sequenceNo);
    }

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.HSE_TI);
    }

    @Override
    public String getPrefix() {
        return "HSETI";
    }

    private int getLastTwoDigitsOfYear(int year) {
        return year % 100;
    }
}
