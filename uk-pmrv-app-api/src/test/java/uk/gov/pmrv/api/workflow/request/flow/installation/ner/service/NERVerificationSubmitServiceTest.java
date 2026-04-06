package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NERVerificationSubmitServiceTest {

    private NERVerificationSubmitService service;

    @BeforeEach
    void setUp() {
        service = new NERVerificationSubmitService();
    }

    @Test
    void applySaveAction_shouldPopulateTaskAndRequestPayloads() {
        // Arrange
        NERVerificationData verificationData = new NERVerificationData();
        Map<String, List<Boolean>> sectionsCompleted = Map.of("sectionA", List.of(true, false));

        NERApplicationVerificationSaveRequestTaskActionPayload actionPayload =
                NERApplicationVerificationSaveRequestTaskActionPayload.builder()
                        .verificationData(verificationData)
                        .verificationSectionsCompleted(sectionsCompleted)
                        .build();

        // Task payload
        NERApplicationVerificationSubmitRequestTaskPayload taskPayload =
                new NERApplicationVerificationSubmitRequestTaskPayload();
        taskPayload.setVerificationReport(new NERVerificationReport());
        taskPayload.setVerificationAttachments(new HashMap<>());

        // Request payload
        NerRequestPayload requestPayload = NerRequestPayload.builder().build();

        Request request = new Request();
        request.setPayload(requestPayload);
        request.setVerificationBodyId(1L);

        RequestTask requestTask = new RequestTask();
        requestTask.setPayload(taskPayload);
        requestTask.setRequest(request);

        // Act
        service.applySaveAction(actionPayload, requestTask);

        // Assert

        // 1. verificationData copied
        assertEquals(
                verificationData,
                taskPayload.getVerificationReport().getVerificationData());

        // 2. sections set on task payload
        assertEquals(sectionsCompleted, taskPayload.getVerificationSectionsCompleted());

        // 3. verification report propagated
        assertEquals(taskPayload.getVerificationReport(), requestPayload.getVerificationReport());

        // 4. verificationBodyId set
        assertEquals(
                1L,
                requestPayload.getVerificationReport().getVerificationBodyId());

        // 5. sections set on request payload
        assertEquals(sectionsCompleted, requestPayload.getVerificationSectionsCompleted());

        // 6. attachments copied
        assertEquals(
                taskPayload.getVerificationAttachments(),
                requestPayload.getVerificationAttachments());
    }
}
