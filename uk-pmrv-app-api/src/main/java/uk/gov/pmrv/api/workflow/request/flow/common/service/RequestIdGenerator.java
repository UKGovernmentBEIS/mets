package uk.gov.pmrv.api.workflow.request.flow.common.service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.util.List;

/**
 * Generates request id according to the RequestType.
 */
public interface RequestIdGenerator {

    String generate(RequestParams params);

    List<RequestType> getTypes();

    /**
     * Prefix used when generating id.
     * @return
     */
    String getPrefix();
}
