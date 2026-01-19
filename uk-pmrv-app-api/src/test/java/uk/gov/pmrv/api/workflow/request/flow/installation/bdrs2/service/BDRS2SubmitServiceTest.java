package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ContinueApplicationForFreeAllocationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2GuardQuestions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BDRS2SubmitServiceTest {

    @InjectMocks
    private BDRS2SubmitService service;

    @Test
    void applySaveAction() {
        final Map<String, Boolean> expectedSectionsCompleted = new HashMap<>();
        expectedSectionsCompleted.put("baseline", false);

        final BDRS2GuardQuestions guardQuestions = BDRS2GuardQuestions.builder()
                .continueApplicationForFreeAllocationType(BDRS2ContinueApplicationForFreeAllocationType.CONTINUE_AS_MAIN_SCHEME_PARTICIPANT)
                .covidAdjustments(Boolean.FALSE)
                .inEiteSector(Boolean.TRUE)
                .build();

        final BDRS2 bdrs2 = BDRS2.builder()
                .bdrs2guardQuestions(guardQuestions)
                .build();

        final BDRS2ApplicationSubmitRequestTaskPayload expectedTaskPayload =
                BDRS2ApplicationSubmitRequestTaskPayload
                        .builder()
                        .build();

        final RequestTask requestTask = RequestTask.builder()
                .payload(expectedTaskPayload)
                .build();

        final BDRS2ApplicationSaveRequestTaskActionPayload expectedTaskActionPayload =
                BDRS2ApplicationSaveRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.BDRS2_APPLICATION_SAVE_PAYLOAD)
                        .bdrs2(bdrs2)
                        .bdrs2SectionsCompleted(expectedSectionsCompleted)
                        .build();

        service.applySaveAction(requestTask, expectedTaskActionPayload);

        assertEquals(expectedTaskPayload.getBdrs2(), expectedTaskActionPayload.getBdrs2());
        assertEquals(expectedTaskPayload.getBdrs2SectionsCompleted(), expectedTaskActionPayload.getBdrs2SectionsCompleted());
    }
}
