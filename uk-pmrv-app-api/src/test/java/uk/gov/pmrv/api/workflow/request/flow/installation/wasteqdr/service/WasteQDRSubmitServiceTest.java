package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationSubmitRequestTaskPayload;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class WasteQDRSubmitServiceTest {

    @InjectMocks
    private WasteQDRSubmitService service;


    @Test
    void applySaveAction() {

        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("test",false);

        final WasteQDRApplicationSubmitRequestTaskPayload expectedTaskPayload =
                WasteQDRApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final WasteQDRApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                WasteQDRApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.WASTE_QDR_APPLICATION_SAVE_PAYLOAD)
                        .qdr(WasteQDR.builder().build())
                        .wasteQDRSectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getQdr(), expectedTaskActionPayload.getQdr());
        assertEquals(expectedTaskPayload.getWasteQDRSectionsCompleted(), expectedTaskActionPayload.getWasteQDRSectionsCompleted());
    }
}
