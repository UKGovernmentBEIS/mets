package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.mapper.WasteQDRMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WasteQDRReviewServiceTest {

    @InjectMocks
    private WasteQDRReviewService service;

    @Mock
    private WasteQDRValidationService validationService;

    @Mock
    private RequestService requestService;

    @Mock
    private WasteQDRMapper mapper;

    @Test
    void saveReviewDecision_setsFieldsCorrectly() {
        WasteQDRReviewDecision reviewDecision = new WasteQDRReviewDecision();
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                new WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload();
        WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload actionPayload =
                new WasteQDRSaveReviewGroupDecisionRequestTaskActionPayload();
        RequestTask requestTask = new RequestTask();

        requestTask.setPayload(taskPayload);
        actionPayload.setReviewDecision(reviewDecision);
        actionPayload.setRegulatorReviewSectionsCompleted(Map.of("sectionA", true));

        service.saveReviewDecision(actionPayload, requestTask);

        assertThat(taskPayload.getReviewDecision()).isEqualTo(reviewDecision);
        assertThat(taskPayload.getRegulatorReviewSectionsCompleted()).containsEntry("sectionA", true);
    }

    @Test
    void returnForAmends_validatesAndAddsAction() {

        WasteQDRRequestPayload requestPayload = new WasteQDRRequestPayload();

        Request request = new Request();
        request.setPayload(requestPayload);

        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload =
                new WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload();
        taskPayload.setRegulatorReviewAttachments(Map.of(UUID.randomUUID(), "file2"));

        RequestTask requestTask = new RequestTask();
        requestTask.setRequest(request);
        requestTask.setPayload(taskPayload);

        AppUser user = new AppUser();
        user.setUserId("regulator");

        service.returnForAmends(requestTask, user);


        verify(validationService).validateReturnForAmends(taskPayload);

        verify(requestService).addActionToRequest(
                eq(request),
                any(WasteQDRRegulatorReviewReturnedForAmendsRequestActionPayload.class),
                eq(RequestActionType.WASTE_QDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS),
                eq("regulator")
        );

        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo("regulator");
    }
}

