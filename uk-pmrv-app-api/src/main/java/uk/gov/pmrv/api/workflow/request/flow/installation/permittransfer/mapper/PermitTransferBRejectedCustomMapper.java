package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitApplicationRejectedCustomMapper;

@Service
public class PermitTransferBRejectedCustomMapper extends PermitApplicationRejectedCustomMapper {

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_TRANSFER_B_APPLICATION_REJECTED;
    }
}
