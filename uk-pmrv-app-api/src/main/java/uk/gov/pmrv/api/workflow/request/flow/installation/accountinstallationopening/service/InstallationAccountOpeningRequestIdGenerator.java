package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.AccountIdBasedRequestIdGenerator;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstallationAccountOpeningRequestIdGenerator extends AccountIdBasedRequestIdGenerator {

    @Override
    public List<RequestType> getTypes() {
        return List.of(RequestType.INSTALLATION_ACCOUNT_OPENING);
    }

    @Override
    public String getPrefix() {
        return "NEW";
    }
}
