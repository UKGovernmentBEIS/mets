package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.HashMap;
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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerOperatorDocumentWithComment;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerOperatorDocuments;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerReviewGroupDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSubmitApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NerApplyReviewServiceTest {

    @InjectMocks
    private NerApplyReviewService service;
    
    @Mock
    private NerReviewValidator nerReviewValidator;

    @Test
    void applySaveAction() {

        final NerApplicationReviewRequestTaskPayload taskPayload =
            NerApplicationReviewRequestTaskPayload.builder()
                .nerOperatorDocuments(NerOperatorDocuments.builder()
                    .newEntrantDataReport(NerOperatorDocumentWithComment.builder().build())
                    .verifierOpinionStatement(NerOperatorDocumentWithComment.builder().build())
                    .monitoringMethodologyPlan(NerOperatorDocumentWithComment.builder().build())
                    .build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .build();

        final UUID newEntrantDataReport = UUID.randomUUID();
        final UUID monitoringMethodologyPlan = UUID.randomUUID();
        final UUID additional = UUID.randomUUID();
        final Map<String, Boolean> sectionsCompleted = Map.of("section1", true);
        final Map<String, Boolean> reviewSectionsCompleted = Map.of("section1", false);
        final NerSaveApplicationReviewRequestTaskActionPayload taskActionPayload =
            NerSaveApplicationReviewRequestTaskActionPayload.builder()
                .newEntrantDataReport(newEntrantDataReport)
                .monitoringMethodologyPlan(monitoringMethodologyPlan)
                .additionalDocuments(AdditionalDocuments.builder().exist(true).documents(Set.of(additional)).build())
                .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
                .nerSectionsCompleted(sectionsCompleted)
                .reviewSectionsCompleted(reviewSectionsCompleted)
                .build();

        service.applySaveAction(requestTask, taskActionPayload);

        Assertions.assertEquals(taskPayload.getNerOperatorDocuments().getNewEntrantDataReport().getDocument(),
            taskActionPayload.getNewEntrantDataReport());
        Assertions.assertEquals(taskPayload.getNerOperatorDocuments().getVerifierOpinionStatement().getDocument(),
            taskActionPayload.getVerifierOpinionStatement());
        Assertions.assertEquals(taskPayload.getNerOperatorDocuments().getMonitoringMethodologyPlan().getDocument(),
            taskActionPayload.getMonitoringMethodologyPlan());
        Assertions.assertEquals(taskPayload.getAdditionalDocuments(), taskActionPayload.getAdditionalDocuments());
        Assertions.assertEquals(taskPayload.getConfidentialityStatement(),
            taskActionPayload.getConfidentialityStatement());
        Assertions.assertEquals(taskPayload.getNerSectionsCompleted(), taskActionPayload.getNerSectionsCompleted());
        Assertions.assertEquals(taskPayload.getReviewSectionsCompleted(), taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void saveReviewGroupDecision() {
        
        final NerSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
            NerSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .group(NerReviewGroup.ADDITIONAL_DOCUMENTS)
                .decision(NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .build())
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();
        
        final Map<NerReviewGroup, NerReviewGroupDecision> decisions = new HashMap<>();
        decisions.put(NerReviewGroup.VERIFIER_OPINION_STATEMENT, NerReviewGroupDecision.builder()
            .type(ReviewDecisionType.ACCEPTED).build());
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .reviewGroupDecisions(decisions)
            .build();
        
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        
        service.saveReviewGroupDecision(taskActionPayload, requestTask);
        
        Assertions.assertEquals(taskPayload.getReviewSectionsCompleted(), taskActionPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(taskPayload.getReviewGroupDecisions(), Map.of(
            NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
            NerReviewGroup.VERIFIER_OPINION_STATEMENT, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
        ));
    }

    @Test
    void saveReviewGroupDecisionAndResetDetermination() {

        final NerSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload =
            NerSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .group(NerReviewGroup.ADDITIONAL_DOCUMENTS)
                .decision(NerReviewGroupDecision.builder()
                    .type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED)
                    .build())
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();

        final Map<NerReviewGroup, NerReviewGroupDecision> decisions = new HashMap<>();
        decisions.put(NerReviewGroup.VERIFIER_OPINION_STATEMENT, NerReviewGroupDecision.builder()
            .type(ReviewDecisionType.ACCEPTED).build());
        final NerProceedToAuthorityDetermination determination =
            NerProceedToAuthorityDetermination.builder().type(NerDeterminationType.PROCEED_TO_AUTHORITY).build();
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .determination(determination)
            .reviewGroupDecisions(decisions)
            .build();

        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        
        when(nerReviewValidator.isReviewDeterminationValid(determination, taskPayload.getReviewGroupDecisions())).thenReturn(false);
        service.saveReviewGroupDecision(taskActionPayload, requestTask);

        Assertions.assertEquals(taskPayload.getReviewSectionsCompleted(), taskActionPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(taskPayload.getReviewGroupDecisions(), Map.of(
            NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.OPERATOR_AMENDS_NEEDED).build(),
            NerReviewGroup.VERIFIER_OPINION_STATEMENT, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
        ));
        assertNull(taskPayload.getDetermination());
    }
    
    @Test
    void saveDetermination() {

        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();
        final NerEndedDetermination determination = NerEndedDetermination.builder().type(NerDeterminationType.CLOSED)
            .reason("reason")
            .build();
        final NerSaveReviewDeterminationRequestTaskActionPayload taskActionPayload =
            NerSaveReviewDeterminationRequestTaskActionPayload.builder()
                .determination(determination)
                .reviewSectionsCompleted(Map.of("section1", true))
                .build();
            
        service.saveDetermination(taskActionPayload, requestTask);

        Assertions.assertEquals(taskPayload.getReviewSectionsCompleted(), taskActionPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(taskPayload.getDetermination(), determination);
    }

    @Test
    void saveRequestPeerReviewAction() {

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final String selectedPeerReview = "selectedPeerReview";
        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .nerOperatorDocuments(NerOperatorDocuments.builder()
                .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
            .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
            .determination(NerEndedDetermination.builder().reason("reason").build())
            .reviewGroupDecisions(Map.of(NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()))
            .nerAttachments(Map.of(UUID.randomUUID(), "att1"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "att2"))
            .nerSectionsCompleted(Map.of("sec1", true))
            .reviewSectionsCompleted(Map.of("sec2", true))
            .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(request).build();

        service.saveRequestPeerReviewAction(requestTask, selectedPeerReview, appUser);

        Assertions.assertEquals(requestPayload.getNerOperatorDocuments(), taskPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestPayload.getConfidentialityStatement(), taskPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestPayload.getAdditionalDocuments(), taskPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestPayload.getDetermination(), taskPayload.getDetermination());
        Assertions.assertEquals(requestPayload.getReviewGroupDecisions(), taskPayload.getReviewGroupDecisions());
        Assertions.assertEquals(requestPayload.getNerAttachments(), taskPayload.getNerAttachments());
        Assertions.assertEquals(requestPayload.getReviewAttachments(), taskPayload.getReviewAttachments());
        Assertions.assertEquals(requestPayload.getNerSectionsCompleted(), taskPayload.getNerSectionsCompleted());
        Assertions.assertEquals(requestPayload.getReviewSectionsCompleted(), taskPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(requestPayload.getRegulatorReviewer(), appUser.getUserId());
        Assertions.assertEquals(requestPayload.getRegulatorPeerReviewer(), selectedPeerReview);
    }

    @Test
    void saveRequestReturnForAmends() {

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .nerOperatorDocuments(NerOperatorDocuments.builder()
                .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
            .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
            .determination(NerEndedDetermination.builder().reason("reason").build())
            .reviewGroupDecisions(Map.of(NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()))
            .nerAttachments(Map.of(UUID.randomUUID(), "att1"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "att2"))
            .nerSectionsCompleted(Map.of("sec1", true))
            .reviewSectionsCompleted(Map.of("sec2", true))
            .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(request).build();

        service.updateRequestPayload(requestTask, appUser);

        Assertions.assertEquals(requestPayload.getNerOperatorDocuments(), taskPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestPayload.getConfidentialityStatement(), taskPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestPayload.getAdditionalDocuments(), taskPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestPayload.getDetermination(), taskPayload.getDetermination());
        Assertions.assertEquals(requestPayload.getReviewGroupDecisions(), taskPayload.getReviewGroupDecisions());
        Assertions.assertEquals(requestPayload.getNerAttachments(), taskPayload.getNerAttachments());
        Assertions.assertEquals(requestPayload.getReviewAttachments(), taskPayload.getReviewAttachments());
        Assertions.assertEquals(requestPayload.getNerSectionsCompleted(), taskPayload.getNerSectionsCompleted());
        Assertions.assertEquals(requestPayload.getReviewSectionsCompleted(), taskPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(requestPayload.getRegulatorReviewer(), appUser.getUserId());
    }

    @Test
    void amend() {

        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final NerSaveApplicationAmendRequestTaskActionPayload taskActionPayload = NerSaveApplicationAmendRequestTaskActionPayload.builder()
            .nerOperatorDocuments(NerOperatorDocuments.builder()
                .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
            .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
            .nerSectionsCompleted(Map.of("sec1", true))
            .reviewSectionsCompleted(Map.of("sec2", true))
            .build();
        final NerApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            NerApplicationAmendsSubmitRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).request(request).build();

        service.amend(taskActionPayload, requestTask);

        Assertions.assertEquals(requestTaskPayload.getNerOperatorDocuments(), taskActionPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestTaskPayload.getConfidentialityStatement(), taskActionPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestTaskPayload.getAdditionalDocuments(), taskActionPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestTaskPayload.getNerSectionsCompleted(), taskActionPayload.getNerSectionsCompleted());
        Assertions.assertEquals(requestTaskPayload.getReviewSectionsCompleted(), taskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void submitAmendedNer() {

        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final NerSubmitApplicationAmendRequestTaskActionPayload taskActionPayload = NerSubmitApplicationAmendRequestTaskActionPayload.builder()
            .nerSectionsCompleted(Map.of("sec1", true))
            .build();
        final NerApplicationAmendsSubmitRequestTaskPayload requestTaskPayload =
            NerApplicationAmendsSubmitRequestTaskPayload.builder()
                .nerOperatorDocuments(NerOperatorDocuments.builder()
                    .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                    .build())
                .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
                .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
                .reviewGroupDecisions(Map.of(NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()))
                .reviewSectionsCompleted(Map.of("sec2", true))
                .reviewAttachments(Map.of(UUID.randomUUID(), "att2"))
                .build();
        final RequestTask requestTask = RequestTask.builder().payload(requestTaskPayload).request(request).build();

        service.submitAmendedNer(taskActionPayload, requestTask);

        Assertions.assertEquals(requestPayload.getNerOperatorDocuments(), requestTaskPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestPayload.getConfidentialityStatement(), requestTaskPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestPayload.getAdditionalDocuments(), requestTaskPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestPayload.getReviewSectionsCompleted(), requestTaskPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(requestPayload.getNerSectionsCompleted(), taskActionPayload.getNerSectionsCompleted());
    }

    @Test
    void saveDecisionNotification() {
        
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .nerOperatorDocuments(NerOperatorDocuments.builder()
                .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
            .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
            .determination(NerEndedDetermination.builder().reason("reason").build())
            .reviewGroupDecisions(Map.of(NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()))
            .nerAttachments(Map.of(UUID.randomUUID(), "att1"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "att2"))
            .nerSectionsCompleted(Map.of("sec1", true))
            .reviewSectionsCompleted(Map.of("sec2", true))
            .build();
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("signatory")
            .externalContacts(Set.of(1L))
            .build();
        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(request)
            .build();
        final AppUser appUser = AppUser.builder().build();
        
        service.saveDecisionNotification(requestTask, decisionNotification, appUser);

        Assertions.assertEquals(requestPayload.getNerOperatorDocuments(), taskPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestPayload.getConfidentialityStatement(), taskPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestPayload.getAdditionalDocuments(), taskPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestPayload.getDetermination(), taskPayload.getDetermination());
        Assertions.assertEquals(requestPayload.getReviewGroupDecisions(), taskPayload.getReviewGroupDecisions());
        Assertions.assertEquals(requestPayload.getNerAttachments(), taskPayload.getNerAttachments());
        Assertions.assertEquals(requestPayload.getReviewAttachments(), taskPayload.getReviewAttachments());
        Assertions.assertEquals(requestPayload.getNerSectionsCompleted(), taskPayload.getNerSectionsCompleted());
        Assertions.assertEquals(requestPayload.getReviewSectionsCompleted(), taskPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(requestPayload.getRegulatorReviewer(), appUser.getUserId());
        Assertions.assertEquals(requestPayload.getDecisionNotification(), decisionNotification);
    }

    @Test
    void completeReview() {

        final AppUser appUser = AppUser.builder().userId("userId").build();
        final NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        final Request request = Request.builder().payload(requestPayload).build();
        final NerApplicationReviewRequestTaskPayload taskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .nerOperatorDocuments(NerOperatorDocuments.builder()
                .newEntrantDataReport(NerOperatorDocumentWithComment.builder().document(UUID.randomUUID()).build())
                .build())
            .confidentialityStatement(ConfidentialityStatement.builder().exist(false).build())
            .additionalDocuments(AdditionalDocuments.builder().exist(false).build())
            .determination(NerEndedDetermination.builder().reason("reason").build())
            .reviewGroupDecisions(Map.of(NerReviewGroup.ADDITIONAL_DOCUMENTS, NerReviewGroupDecision.builder().type(ReviewDecisionType.ACCEPTED).build()))
            .nerAttachments(Map.of(UUID.randomUUID(), "att1"))
            .reviewAttachments(Map.of(UUID.randomUUID(), "att2"))
            .nerSectionsCompleted(Map.of("sec1", true))
            .reviewSectionsCompleted(Map.of("sec2", true))
            .build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).request(request).build();

        service.updateRequestPayload(requestTask, appUser);

        Assertions.assertEquals(requestPayload.getNerOperatorDocuments(), taskPayload.getNerOperatorDocuments());
        Assertions.assertEquals(requestPayload.getConfidentialityStatement(), taskPayload.getConfidentialityStatement());
        Assertions.assertEquals(requestPayload.getAdditionalDocuments(), taskPayload.getAdditionalDocuments());
        Assertions.assertEquals(requestPayload.getDetermination(), taskPayload.getDetermination());
        Assertions.assertEquals(requestPayload.getReviewGroupDecisions(), taskPayload.getReviewGroupDecisions());
        Assertions.assertEquals(requestPayload.getNerAttachments(), taskPayload.getNerAttachments());
        Assertions.assertEquals(requestPayload.getReviewAttachments(), taskPayload.getReviewAttachments());
        Assertions.assertEquals(requestPayload.getNerSectionsCompleted(), taskPayload.getNerSectionsCompleted());
        Assertions.assertEquals(requestPayload.getReviewSectionsCompleted(), taskPayload.getReviewSectionsCompleted());
        Assertions.assertEquals(requestPayload.getRegulatorReviewer(), appUser.getUserId());
    }
}
