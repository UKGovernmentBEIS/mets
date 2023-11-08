package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerAuthorityResponseRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveAuthorityResponseRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.RejectAuthorityResponse;

@ExtendWith(MockitoExtension.class)
class NerAuthorityResponseServiceTest {

    @InjectMocks
    private NerAuthorityResponseService service;


    @Test
    void applyAuthorityResponseSaveAction() {

        final NerAuthorityResponseRequestTaskPayload taskPayload =
            NerAuthorityResponseRequestTaskPayload.builder().build();
        final RequestTask requestTask = RequestTask.builder().payload(taskPayload).build();

        final RejectAuthorityResponse authorityResponse =
            RejectAuthorityResponse.builder().monitoringMethodologyPlanApproved(false).rejectComments("comment")
                .build();
        final LocalDate authorityDate = LocalDate.of(2021, 2, 1);
        final NerSaveAuthorityResponseRequestTaskActionPayload taskActionPayload =
            NerSaveAuthorityResponseRequestTaskActionPayload.builder()
                .submittedToAuthorityDate(authorityDate)
                .authorityResponse(authorityResponse)
                .build();

        service.applyAuthorityResponseSaveAction(requestTask, taskActionPayload);

        Assertions.assertEquals(taskPayload.getSubmittedToAuthorityDate(),
            taskActionPayload.getSubmittedToAuthorityDate());
        Assertions.assertEquals(taskPayload.getAuthorityResponse(), taskActionPayload.getAuthorityResponse());
    }
}
