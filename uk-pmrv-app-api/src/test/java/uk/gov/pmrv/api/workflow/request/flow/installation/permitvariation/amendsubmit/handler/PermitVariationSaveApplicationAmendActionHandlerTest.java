package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.service.PermitVariationAmendService;

@ExtendWith(MockitoExtension.class)
class PermitVariationSaveApplicationAmendActionHandlerTest {

    @InjectMocks
    private PermitVariationSaveApplicationAmendActionHandler permitVariationSaveApplicationAmendActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitVariationAmendService permitVariationAmendService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload permitAmendRequestTaskActionPayload =
            PermitVariationApplicationSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND_PAYLOAD)
                .permit(Permit.builder().
                    environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
                    .build())
                .build();

        RequestTask requestTask = RequestTask.builder().id(requestTaskId).build();
        AppUser appUser = AppUser.builder().build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        permitVariationSaveApplicationAmendActionHandler
            .process(requestTask.getId(), RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND, appUser, permitAmendRequestTaskActionPayload);

        //verify
        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(permitVariationAmendService, times(1)).amendPermitVariation(permitAmendRequestTaskActionPayload, requestTask);
    }

    @Test
    void getTypes() {
        assertThat(permitVariationSaveApplicationAmendActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND);
    }
}