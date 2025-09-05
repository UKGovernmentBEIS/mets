package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIApplicationAmendsSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class HSETIAmendsSubmitService {

    private final HSETISubmitService submitService;

    @Transactional
    public void saveAmends(HSETIApplicationAmendsSaveRequestTaskActionPayload taskActionPayload,
                           RequestTask requestTask) {
        HSETIApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            (HSETIApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setHseti(taskActionPayload.getHseti());
        requestTaskPayload
            .setHsetiSectionsCompleted(taskActionPayload.getHsetiSectionsCompleted());
        requestTaskPayload
            .setRequestedChangesCompleted(taskActionPayload.getRequestedChangesCompleted());
    }

    @Transactional
    public void submitToRegulator(HSETIApplicationAmendsSubmitRequestTaskActionPayload actionPayload,
                                  RequestTask requestTask, AppUser appUser) {

        ((HSETIApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload())
                .setHsetiSectionsCompleted(actionPayload.getHsetiSectionsCompleted());

        submitService.submitToRegulator(requestTask, appUser);
    }
}
