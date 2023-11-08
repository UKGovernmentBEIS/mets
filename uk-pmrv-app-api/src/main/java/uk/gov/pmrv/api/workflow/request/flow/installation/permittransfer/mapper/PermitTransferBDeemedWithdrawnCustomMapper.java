package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.mapper;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.mapper.PermitApplicationDeemedWithdrawnCustomMapper;

@Service
public class PermitTransferBDeemedWithdrawnCustomMapper extends PermitApplicationDeemedWithdrawnCustomMapper {

    @Override
    public RequestActionType getRequestActionType() {
        return RequestActionType.PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN;
    }
}
