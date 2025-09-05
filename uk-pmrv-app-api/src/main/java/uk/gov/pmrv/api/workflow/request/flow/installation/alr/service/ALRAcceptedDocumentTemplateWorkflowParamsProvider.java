package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateWorkflowParamsProvider;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.enums.DoalDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.*;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonGroupType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.ArticleReasonItemType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.enums.DoalAuthorityResponseType;


import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

@Component
public class ALRAcceptedDocumentTemplateWorkflowParamsProvider implements DocumentTemplateWorkflowParamsProvider<ALRRequestPayload> {

    @Override
    public DocumentTemplateGenerationContextActionType getContextActionType() {
        return DocumentTemplateGenerationContextActionType.ALR_ACCEPTED;
    }

    @Override
    public Map<String, Object> constructParams(ALRRequestPayload payload, String requestId) {

        //TODO: Change when alr template arrives ~ use DUMMY data until then

        return Map.of(
                "reportingYear", Year.now(),
                "doal", Doal.builder().activityLevelChangeInformation(ActivityLevelChangeInformation.builder()
                                .preliminaryAllocations((new TreeSet<>()))
                                .activityLevels(new ArrayList<>())
                                .areConservativeEstimates(false)
                                .build())
                        .operatorActivityLevelReport(OperatorActivityLevelReport.builder().areActivityLevelsEstimated(false)
                                .comment("dummy comment").build())
                        .verificationReportOfTheActivityLevelReport(VerificationReportOfTheActivityLevelReport.builder()
                                .comment("dummy comment").build())
                        .additionalDocuments(DoalAdditionalDocuments.builder().exist(false).build())
                        .determination(DoalProceedToAuthorityDetermination.builder()
                                .needsOfficialNotice(false)
                                .hasWithholdingOfAllowances(false)
                                .type(DoalDeterminationType.PROCEED_TO_AUTHORITY)
                                .reason("reason")
                                .articleReasonGroupType(ArticleReasonGroupType.ARTICLE_6A_REASONS)
                                .articleReasonItems(Set.of(ArticleReasonItemType.ALLOCATION_ADJUSTMENT_UNDER_ARTICLE_5))
                                .build())
                        .build(),
                "authorityResponse", DoalGrantAuthorityResponse.builder().type(DoalAuthorityResponseType.VALID).authorityRespondDate(LocalDate.now()).build());
    }
}
