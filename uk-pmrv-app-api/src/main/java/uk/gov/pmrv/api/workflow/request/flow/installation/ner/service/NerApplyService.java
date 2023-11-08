package uk.gov.pmrv.api.workflow.request.flow.installation.ner.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.mapper.NerMapper;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerSubmitValidator;

@Service
@RequiredArgsConstructor
public class NerApplyService {

    private static final NerMapper NER_MAPPER = Mappers.getMapper(NerMapper.class);

    private final NerSubmitValidator validatorService;
    private final RequestService requestService;

    @Transactional
    public void applySaveAction(final RequestTask requestTask,
                                final NerSaveApplicationRequestTaskActionPayload taskActionPayload) {

        final NerApplicationSubmitRequestTaskPayload
            requestTaskPayload = (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setNerOperatorDocuments(taskActionPayload.getNerOperatorDocuments());
        requestTaskPayload.setConfidentialityStatement(taskActionPayload.getConfidentialityStatement());
        requestTaskPayload.setAdditionalDocuments(taskActionPayload.getAdditionalDocuments());
        requestTaskPayload.setNerSectionsCompleted(taskActionPayload.getNerSectionsCompleted());
    }

    @Transactional
    public void applySubmitAction(final RequestTask requestTask, final PmrvUser pmrvUser) {

        final Request request = requestTask.getRequest();
        final NerApplicationSubmitRequestTaskPayload taskPayload =
            (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        // validate
        validatorService.validateSubmitTaskPayload(taskPayload);

        // update request payload
        final NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();
        requestPayload.setNerOperatorDocuments(taskPayload.getNerOperatorDocuments());
        requestPayload.setConfidentialityStatement(taskPayload.getConfidentialityStatement());
        requestPayload.setAdditionalDocuments(taskPayload.getAdditionalDocuments());
        requestPayload.setNerAttachments(taskPayload.getNerAttachments());
        requestPayload.setNerSectionsCompleted(taskPayload.getNerSectionsCompleted());

        // add action
        final NerApplicationSubmittedRequestActionPayload actionPayload =
            NER_MAPPER.toNerApplicationSubmitted(taskPayload);
        requestService.addActionToRequest(
            request,
            actionPayload,
            RequestActionType.NER_APPLICATION_SUBMITTED,
            pmrvUser.getUserId()
        );
    }
}
