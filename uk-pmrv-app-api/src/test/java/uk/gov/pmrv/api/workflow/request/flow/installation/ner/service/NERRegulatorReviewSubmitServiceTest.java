package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.account.installation.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestVerificationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewSaveTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationRegulatorReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NER;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERFiles;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NERReviewOpinion;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERValidationService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
public class NERRegulatorReviewSubmitServiceTest {

    @Mock
    private NERValidationService nerValidationService;

    @Mock
    private RequestService requestService;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private RequestVerificationService requestVerificationService;

    @InjectMocks
    private NERRegulatorReviewSubmitService service;

    @Test
    void saveReviewGroupDecision_shouldUpdateTaskPayload() {
        // given
        NERReviewGroup group = NERReviewGroup.NER; // adjust enum if needed
        NERReviewDecision decision = NERReviewDecision.builder().build();

        Map<String, Boolean> sectionsCompleted = Map.of("section1", true);

        NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload payload =
                mock(NERSaveRegulatorReviewGroupDecisionRequestTaskActionPayload.class);

        when(payload.getGroup()).thenReturn(group);
        when(payload.getDecision()).thenReturn(decision);
        when(payload.getRegulatorReviewSectionsCompleted()).thenReturn(sectionsCompleted);

        Map<NERReviewGroup, NERReviewDecision> decisionsMap = new HashMap<>();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                new NERApplicationRegulatorReviewSubmitRequestTaskPayload();
        taskPayload.setRegulatorReviewGroupDecisions(decisionsMap);

        RequestTask requestTask = new RequestTask();
        requestTask.setPayload(taskPayload);

        // when
        service.saveReviewGroupDecision(payload, requestTask);

        // then
        assertEquals(decision, decisionsMap.get(group));
        assertEquals(sectionsCompleted, taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void returnForAmends() {
        // given
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .build();

        Request request = Request.builder().payload(NerRequestPayload.builder().build()).build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewAttachments(Map.of(UUID.randomUUID(), "file"))
                        .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .payload(payload)
                .build();

        // when
        service.returnForAmends(requestTask, appUser);

        // then
        verify(nerValidationService).validateReturnForAmends(payload);

        verify(requestService).addActionToRequest(
                eq(request),
                any(NERRegulatorReviewReturnedForAmendsRequestActionPayload.class),
                eq(RequestActionType.NER_APPLICATION_RETURNED_FOR_AMENDS),
                eq("userId")
        );
    }

    @Test
    void save_shouldUpdateTaskPayload() {
        NERApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder().build();

        RequestTask requestTask = new RequestTask();
        requestTask.setPayload(taskPayload);

        NERApplicationRegulatorReviewSaveTaskActionPayload payload =
                NERApplicationRegulatorReviewSaveTaskActionPayload.builder()
                        .regulatorReviewOutcome(NERApplicationRegulatorReviewOutcome.builder()
                                .notes("notes")
                                .build())
                        .regulatorReviewSectionsCompleted(Map.of("section", true))
                        .build();

        // when
        service.save(payload, requestTask);

        // then
        assertEquals(
                payload.getRegulatorReviewOutcome(),
                taskPayload.getRegulatorReviewOutcome()
        );

        assertEquals(
                payload.getRegulatorReviewSectionsCompleted(),
                taskPayload.getRegulatorReviewSectionsCompleted()
        );
    }

    @Test
    void completeApplication() {
        Request request = new Request();
        NerRequestPayload requestPayload = NerRequestPayload.builder().ner(NER.builder().nerFiles(NERFiles.builder().build()).build()).build();
        request.setPayload(requestPayload);
        request.setId("REQ1");

        NERApplicationRegulatorReviewOutcome outcome =
                NERApplicationRegulatorReviewOutcome.builder()
                        .opinion(NERReviewOpinion.PROCEED_TO_AUTHORITY)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(outcome)
                        .build();

        RequestTask requestTask = new RequestTask();
        requestTask.setRequest(request);
        requestTask.setPayload(payload);

        AppUser appUser = AppUser.builder().build();

        when(requestService.findRequestById("REQ1")).thenReturn(request);

        service.completeApplication(requestTask, appUser);

        verify(nerValidationService).validateRegulatorReviewOutcome(
                payload,
                NERReviewOpinion.PROCEED_TO_AUTHORITY
        );

        verify(requestService).findRequestById("REQ1");

        verify(requestService).addActionToRequest(
                eq(request),
                any(NERApplicationCompletedRequestActionPayload.class),
                eq(RequestActionType.NER_APPLICATION_COMPLETED),
                any()
        );
    }

    @Test
    void deemWithdrawn() {
        Request request = new Request();
        NerRequestPayload requestPayload = NerRequestPayload.builder().build();
        request.setPayload(requestPayload);
        request.setId("REQ1");

        NERApplicationRegulatorReviewOutcome outcome =
                NERApplicationRegulatorReviewOutcome.builder()
                        .opinion(NERReviewOpinion.WITHDRAW)
                        .build();

        NERApplicationRegulatorReviewSubmitRequestTaskPayload payload =
                NERApplicationRegulatorReviewSubmitRequestTaskPayload.builder()
                        .regulatorReviewOutcome(outcome)
                        .build();

        RequestTask requestTask = new RequestTask();
        requestTask.setRequest(request);
        requestTask.setPayload(payload);

        AppUser appUser = AppUser.builder().build();
        when(requestService.findRequestById("REQ1")).thenReturn(request);


        service.deemWithdrawn(requestTask, appUser);

        verify(nerValidationService).validateRegulatorReviewOutcome(
                payload,
                NERReviewOpinion.WITHDRAW
        );

        verify(requestService).findRequestById("REQ1");

        verify(requestService).addActionToRequest(
                eq(request),
                any(NERApplicationCompletedRequestActionPayload.class),
                eq(RequestActionType.NER_APPLICATION_DEEMED_WITHDRAWN),
                any()
        );
    }

    @Test
    void prepareRequestPayloadForReopening_whenRegulatorFileDifferent_shouldTransferFileAndIncrementVersion() {
        UUID regulatorFile = UUID.randomUUID();
        UUID operatorFile = UUID.randomUUID();

        NerRequestPayload payload = NerRequestPayload.builder()
                .ner(NER.builder()
                        .nerFiles(NERFiles.builder()
                                .file(operatorFile)
                                .build())
                        .build())
                .regulatorReviewOutcome(NERApplicationRegulatorReviewOutcome.builder()
                        .nerFile(regulatorFile)
                        .build())
                .build();

        payload.setNerAttachments(new HashMap<>());
        payload.setRegulatorReviewAttachments(
                new HashMap<>(Map.of(regulatorFile, "review-file.xlsx")));

        Integer initialVersion = payload.getNerFileVersion();

        service.prepareRequestPayloadForReopening(payload);

        assertThat(payload.getNerFileVersion())
                .isEqualTo(initialVersion + 1);

        assertThat(payload.getNer().getNerFiles().getFile())
                .isEqualTo(regulatorFile);

        assertThat(payload.getNerAttachments())
                .containsEntry(regulatorFile, "review-file.xlsx");

        assertThat(payload.getRegulatorReviewAttachments())
                .doesNotContainKey(regulatorFile);

        assertThat(payload.getRegulatorReviewOutcome().getNerFile())
                .isNull();
    }

    @Test
    void prepareRequestPayloadForReopening_whenFilesAreSame_shouldDoNothing() {
        UUID file = UUID.randomUUID();

        NerRequestPayload payload = NerRequestPayload.builder()
                .ner(NER.builder()
                        .nerFiles(NERFiles.builder()
                                .file(file)
                                .build())
                        .build())
                .regulatorReviewOutcome(NERApplicationRegulatorReviewOutcome.builder()
                        .nerFile(file)
                        .build())
                .build();

        payload.setNerAttachments(new HashMap<>());
        payload.setRegulatorReviewAttachments(
                new HashMap<>(Map.of(file, "review-file.xlsx")));

        Integer initialVersion = payload.getNerFileVersion();

        service.prepareRequestPayloadForReopening(payload);

        assertThat(payload.getNerFileVersion())
                .isEqualTo(initialVersion);

        assertThat(payload.getNer().getNerFiles().getFile())
                .isEqualTo(file);

        assertThat(payload.getRegulatorReviewAttachments())
                .containsKey(file);

        assertThat(payload.getRegulatorReviewOutcome().getNerFile())
                .isEqualTo(file);
    }

    @Test
    void prepareRequestPayloadForReopening_whenRegulatorFileIsNull_shouldDoNothing() {
        NerRequestPayload payload = NerRequestPayload.builder()
                .regulatorReviewOutcome(
                        NERApplicationRegulatorReviewOutcome.builder()
                                .nerFile(null)
                                .build())
                .build();

        service.prepareRequestPayloadForReopening(payload);

        assertThat(payload.getRegulatorReviewOutcome().getNerFile())
                .isNull();
    }
}
