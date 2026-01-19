package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WasteQDRAmendsSubmitServiceTest {

    @InjectMocks
    private WasteQDRAmendsSubmitService amendsSubmitService;

    @Mock
    private WasteQDRSubmitService submitService;

    @Mock
    private WasteQDRValidationService validationService;

    @Test
    public void saveAmends() {
        WasteQDRApplicationAmendsSaveRequestTaskActionPayload taskActionPayload = WasteQDRApplicationAmendsSaveRequestTaskActionPayload
                .builder()
                .qdr(WasteQDR.builder().build())
                .wasteQDRSectionsCompleted(Map.of("test",true))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        WasteQDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = (WasteQDRApplicationAmendsSubmitRequestTaskPayload) WasteQDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .build();

        amendsSubmitService.saveAmends(taskActionPayload, requestTask);

        assertThat(requestTaskPayload.getQdr()).isEqualTo(taskActionPayload.getQdr());

        assertThat(requestTaskPayload.getWasteQDRSectionsCompleted()).isEqualTo(taskActionPayload.getWasteQDRSectionsCompleted());
        assertThat(requestTaskPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskActionPayload.getRegulatorReviewSectionsCompleted());

    }

    @Test
    public void submitToRegulator() {
        final AppUser user = AppUser.builder().userId("user").build();

        WasteQDRApplicationAmendsSubmitRequestTaskPayload requestTaskPayload = (WasteQDRApplicationAmendsSubmitRequestTaskPayload) WasteQDRApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .build();

        final WasteQDRApplicationAmendsSubmitRequestTaskActionPayload payload = WasteQDRApplicationAmendsSubmitRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD)
                .wasteQDRSectionsCompleted(Map.of("test",true))
                .build();

        WasteQDRRequestPayload requestPayload = WasteQDRRequestPayload.builder().build();
        Request request = Request.builder().type(RequestType.WASTE_QDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .type(RequestTaskType.WASTE_QDR_APPLICATION_AMENDS_SUBMIT)
                .payload(requestTaskPayload)
                .request(request)
                .build();

        amendsSubmitService.submitToRegulator(payload, requestTask,user);

        verify(validationService, times(1)).validateWasteQDR(requestTaskPayload.getQdr());

        assertThat(( (WasteQDRApplicationAmendsSubmitRequestTaskPayload) requestTask.getPayload()).getWasteQDRSectionsCompleted())
                .containsExactlyEntriesOf(payload.getWasteQDRSectionsCompleted());
    }
}
