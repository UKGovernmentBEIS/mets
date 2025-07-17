package uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.handler;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountAmendService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskValidationService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.mapper.InstallationAccountPayloadMapper;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InstallationAccountOpeningAmendApplicationActionHandler
    implements RequestTaskActionHandler<InstallationAccountOpeningAmendApplicationRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final InstallationAccountAmendService installationAccountAmendService;
    private final RequestTaskValidationService requestTaskValidationService;
    private static final InstallationAccountPayloadMapper INSTALLATION_ACCOUNT_PAYLOAD_MAPPER = Mappers.getMapper(InstallationAccountPayloadMapper.class);

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final AppUser appUser,
                        final InstallationAccountOpeningAmendApplicationRequestTaskActionPayload payload) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        Request request = requestTask.getRequest();
        
        InstallationAccountPayload currentAccountPayload =
                ((InstallationAccountOpeningApplicationRequestTaskPayload) requestTask.getPayload()).getAccountPayload();
        InstallationAccountDTO currentAccountDTO = INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toAccountInstallationDTO(currentAccountPayload);
        
        InstallationAccountPayload newAccountPayload = payload.getAccountPayload();
        InstallationAccountDTO newAccountDTO = INSTALLATION_ACCOUNT_PAYLOAD_MAPPER.toAccountInstallationDTO(newAccountPayload);
        
        //amend account
        newAccountDTO = installationAccountAmendService.amendAccount(request.getAccountId(), currentAccountDTO, newAccountDTO, appUser);

        // enhance account payload with full legal entity info if id only provided
        if(newAccountPayload.getLegalEntity().getId() != null) {
            newAccountPayload.setLegalEntity(newAccountDTO.getLegalEntity());
        }

        //update request task payload with new account payload
        InstallationAccountOpeningApplicationRequestTaskPayload newInstallationAccountOpeningApplicationRequestTaskPayload = INSTALLATION_ACCOUNT_PAYLOAD_MAPPER
                .toInstallationAccountOpeningApplicationRequestTaskPayload(newAccountPayload);
        requestTaskValidationService.validateRequestTaskPayload(newInstallationAccountOpeningApplicationRequestTaskPayload);
        requestTaskService.updateRequestTaskPayload(requestTask,
                newInstallationAccountOpeningApplicationRequestTaskPayload);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION);
    }
}
