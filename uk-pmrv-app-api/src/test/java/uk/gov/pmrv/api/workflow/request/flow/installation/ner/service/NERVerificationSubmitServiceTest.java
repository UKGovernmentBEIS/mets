package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationData;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERVerificationReport;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NERMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERVerificationSubmitServiceTest {

    @InjectMocks
    private NERVerificationSubmitService service;


    @Mock
    private NERValidationService nerValidationService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private NERMapper nerMapper;

    @Mock
    private RequestService requestService;

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

    @Test
    void sendToOperator_shouldValidateUpdateAndTriggerAction() {
        // Arrange
        AppUser appUser = new AppUser();
        appUser.setUserId("user-1");

        // --- task payload ---
        NERApplicationVerificationSubmitRequestTaskPayload taskPayload =
                new NERApplicationVerificationSubmitRequestTaskPayload();

        NERVerificationReport report = new NERVerificationReport();
        taskPayload.setVerificationReport(report);
        taskPayload.setVerificationSectionsCompleted(Map.of("A", List.of(true)));
        taskPayload.setVerificationAttachments(new HashMap<>());
        taskPayload.setNerAttachments(new HashMap<>());

        // --- request payload ---
        NerRequestPayload requestPayload = NerRequestPayload.builder().build();

        Request request = new Request();
        request.setPayload(requestPayload);
        request.setVerificationBodyId(1L);
        request.setAccountId(100L);

        RequestTask requestTask = new RequestTask();
        requestTask.setRequest(request);
        requestTask.setPayload(taskPayload);

        // --- external dependencies ---
        InstallationOperatorDetails operatorDetails = new InstallationOperatorDetails();

        NERApplicationVerificationSubmittedRequestActionPayload actionPayload =
                new NERApplicationVerificationSubmittedRequestActionPayload();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(100L))
                .thenReturn(operatorDetails);

        when(nerMapper.toNERApplicationVerificationSubmittedRequestActionPayload(taskPayload))
                .thenReturn(actionPayload);

        // Act
        service.sendToOperator(requestTask, appUser);

        // Assert

        // 1. validation called
        verify(nerValidationService).validateVerificationReport(report);

        // 2. request payload updated
        assertTrue(requestPayload.isVerificationPerformed());
        assertEquals(report, requestPayload.getVerificationReport());
        assertEquals(1L, requestPayload.getVerificationReport().getVerificationBodyId());
        assertEquals(taskPayload.getVerificationSectionsCompleted(),
                requestPayload.getVerificationSectionsCompleted());
        assertEquals(taskPayload.getVerificationAttachments(),
                requestPayload.getVerificationAttachments());

        // 3. action triggered
        verify(requestService).addActionToRequest(
                eq(request),
                eq(actionPayload),
                eq(RequestActionType.NER_APPLICATION_VERIFICATION_SUBMITTED),
                eq("user-1")
        );

        // 4. enrichment applied
        assertEquals(operatorDetails, actionPayload.getInstallationOperatorDetails());
        assertEquals(taskPayload.getNerAttachments(), actionPayload.getNerAttachments());
    }
}
