package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRValidationService;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WasteQDRRegulatorReviewSubmitServiceTest {

    @InjectMocks
    private WasteQDRRegulatorReviewSubmitService submitService;

    @Mock
    private WasteQDRValidationService validationService;

    @Test
    void submit() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        WasteQDRRequestPayload requestPayload = WasteQDRRequestPayload
                .builder()
                .build();

        Request request = Request.builder().type(RequestType.WASTE_QDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        submitService.submit(requestTask,user);

        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());
    }

    @Test
    void updateRequestPayload() {
        UUID attachmentId = UUID.randomUUID();
        AppUser user = AppUser.builder().build();
        WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload taskPayload = WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                .builder()
                .regulatorReviewAttachments(Map.of(attachmentId,"test"))
                .regulatorReviewSectionsCompleted(Map.of("test",true))
                .build();

        WasteQDRRequestPayload requestPayload = WasteQDRRequestPayload
                .builder()
                .build();

        Request request = Request.builder().type(RequestType.WASTE_QDR).payload(requestPayload).build();

        RequestTask requestTask = RequestTask
                .builder()
                .payload(taskPayload)
                .request(request)
                .build();

        submitService.updateRequestPayload(requestTask,taskPayload, user);

        assertThat(requestPayload.getRegulatorReviewer()).isEqualTo(user.getUserId());
        assertThat(requestPayload.getRegulatorReviewAttachments()).isEqualTo(taskPayload.getRegulatorReviewAttachments());
        assertThat(requestPayload.getRegulatorReviewSectionsCompleted()).isEqualTo(taskPayload.getRegulatorReviewSectionsCompleted());
    }
}
