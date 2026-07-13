package uk.gov.pmrv.api.workflow.request.flow.installation.doal.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalProceedToAuthorityDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;

import java.util.Map;

@Component
public class DoalAcceptedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<DoalRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.DOAL_ACCEPTED;
    }

    @Override
    public Map<String, Object> constructParams(DoalRequestPayload payload, String requestId) {

        DoalProceedToAuthorityDetermination determination = (DoalProceedToAuthorityDetermination) payload.getDoal().getDetermination();
        boolean hasNERAndYear0Option = determination.getArticleReasonGroupType() == ArticleReasonGroupType.ARTICLE_5_REASONS &&
                payload.getDoal().getActivityLevelChangeInformation()
                        .getActivityLevels()
                        .stream()
                        .anyMatch(activityLevel ->
                                activityLevel.getChangeType() ==
                                        ChangeType.NER_ALLOCATION_FOR_YEAR_0_BASED_ON_ACTIVITY_LEVEL_AL_IN_YEAR_0);

        return Map.of(
                "reportingYear", payload.getReportingYear(),
                "doal", payload.getDoal(),
                "authorityResponse", payload.getDoalAuthority().getAuthorityResponse(),
                "nerAndYear0", hasNERAndYear0Option
        );
    }
}
