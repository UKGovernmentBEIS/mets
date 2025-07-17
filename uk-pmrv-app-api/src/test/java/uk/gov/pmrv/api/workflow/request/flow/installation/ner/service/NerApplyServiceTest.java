package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerOperatorDocumentWithComment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerOperatorDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerSubmitValidator;

@ExtendWith(MockitoExtension.class)
class NerApplyServiceTest {

    @InjectMocks
    private NerApplyService service;

    @Mock
    private NerSubmitValidator validatorService;

    @Mock
    private RequestService requestService;

    @Test
    void applySaveAction() {

        final NerApplicationSubmitRequestTaskPayload taskPayload =
            NerApplicationSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        final UUID newEntrantDataReport = UUID.randomUUID();
        final UUID monitoringMethodologyPlan = UUID.randomUUID();
        final UUID additional = UUID.randomUUID();
        final Map<String, Boolean> sectionsCompleted = Map.of("section1", true);
        final NerSaveApplicationRequestTaskActionPayload taskActionPaylod =
            NerSaveApplicationRequestTaskActionPayload.builder()
                .nerOperatorDocuments(NerOperatorDocuments.builder()
                    .newEntrantDataReport(
                        NerOperatorDocumentWithComment.builder().document(newEntrantDataReport).build())
                    .monitoringMethodologyPlan(
                        NerOperatorDocumentWithComment.builder().document(monitoringMethodologyPlan).build())
                    .build())
                .additionalDocuments(AdditionalDocuments.builder().exist(true).documents(Set.of(additional)).build())
                .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
                .nerSectionsCompleted(sectionsCompleted)
                .build();

        service.applySaveAction(requestTask, taskActionPaylod);

        Assertions.assertEquals(taskPayload.getNerOperatorDocuments(), taskActionPaylod.getNerOperatorDocuments());
        Assertions.assertEquals(taskPayload.getAdditionalDocuments(), taskActionPaylod.getAdditionalDocuments());
        Assertions.assertEquals(taskPayload.getConfidentialityStatement(),
            taskActionPaylod.getConfidentialityStatement());
        Assertions.assertEquals(taskPayload.getNerSectionsCompleted(), taskActionPaylod.getNerSectionsCompleted());
    }

    @Test
    void applySubmitAction() {

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final UUID newEntrantDataReport = UUID.randomUUID();
        final UUID monitoringMethodologyPlan = UUID.randomUUID();
        final UUID additional = UUID.randomUUID();
        final Map<String, Boolean> sectionsCompleted = Map.of("section1", true);
        final NerApplicationSubmitRequestTaskPayload taskPayload =
            NerApplicationSubmitRequestTaskPayload.builder()
                .nerOperatorDocuments(NerOperatorDocuments.builder()
                    .newEntrantDataReport(
                        NerOperatorDocumentWithComment.builder().document(newEntrantDataReport).build())
                    .monitoringMethodologyPlan(
                        NerOperatorDocumentWithComment.builder().document(monitoringMethodologyPlan).build())
                    .build())
                .additionalDocuments(AdditionalDocuments.builder().exist(true).documents(Set.of(additional)).build())
                .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
                .nerAttachments(Map.of(newEntrantDataReport, "newEntrantDataReport", monitoringMethodologyPlan, "monitoringMethodologyPlan", additional, "additional"))
                .nerSectionsCompleted(sectionsCompleted)
                .build();

        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(request)
            .build();
        final NerApplicationSubmittedRequestActionPayload actionPayload =
            NerApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.NER_APPLICATION_SUBMITTED_PAYLOAD)
                .nerOperatorDocuments(NerOperatorDocuments.builder()
                    .newEntrantDataReport(
                        NerOperatorDocumentWithComment.builder().document(newEntrantDataReport).build())
                    .monitoringMethodologyPlan(
                        NerOperatorDocumentWithComment.builder().document(monitoringMethodologyPlan).build())
                    .build())
                .additionalDocuments(AdditionalDocuments.builder().exist(true).documents(Set.of(additional)).build())
                .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
                .nerAttachments(Map.of(newEntrantDataReport, "newEntrantDataReport", monitoringMethodologyPlan, "monitoringMethodologyPlan", additional, "additional"))
                .nerSectionsCompleted(sectionsCompleted)
                .build();


        service.applySubmitAction(requestTask, appUser);

        verify(validatorService, times(1)).validateSubmitTaskPayload(taskPayload);
        verify(requestService, times(1)).addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NER_APPLICATION_SUBMITTED,
            "userId"
        );

        assertEquals(taskPayload.getNerOperatorDocuments(), requestPayload.getNerOperatorDocuments());
        assertEquals(taskPayload.getAdditionalDocuments(), requestPayload.getAdditionalDocuments());
        assertEquals(taskPayload.getConfidentialityStatement(), requestPayload.getConfidentialityStatement());
        assertEquals(taskPayload.getNerAttachments(), requestPayload.getNerAttachments());
        assertEquals(taskPayload.getNerSectionsCompleted(), requestPayload.getNerSectionsCompleted());
    }
}
