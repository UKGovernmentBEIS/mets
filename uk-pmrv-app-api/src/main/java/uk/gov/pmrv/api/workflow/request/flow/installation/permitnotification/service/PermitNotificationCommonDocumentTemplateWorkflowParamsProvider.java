package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationCompletedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.CessationNotification;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecisionDetails;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class PermitNotificationCommonDocumentTemplateWorkflowParamsProvider {


    public Map<String, Object> constructParams(final PermitNotificationRequestPayload payload) {
        Map<String, Object> templateParams = new java.util.HashMap<>(Map.of(
                "officialNotice", ((PermitNotificationReviewDecisionDetails) payload.getReviewDecision().getDetails()).getOfficialNotice()
        ));

        templateParams.putAll(constructReviewDecisionTypeParamsParams(payload.getReviewDecision().getType()));

        templateParams.put("cessationDate", null);
        templateParams.put("resumptionDate", null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ROOT);

        Optional.ofNullable(payload.getPermitNotification())
                .filter(CessationNotification.class::isInstance)
                .map(CessationNotification.class::cast)
                .map(CessationNotification::getDateOfNonCompliance)
                .ifPresent(nonCompliancePeriod -> {
                    Optional.ofNullable(nonCompliancePeriod.getStartDateOfNonCompliance())
                            .ifPresent(startDate -> templateParams.put("cessationDate", startDate.format(formatter)));

                    Optional.ofNullable(nonCompliancePeriod.getEndDateOfNonCompliance())
                            .ifPresent(endDate -> templateParams.put("resumptionDate", endDate.format(formatter)));
                });

        templateParams.put("followUpRequired", false);
        Optional.ofNullable(payload.getReviewDecision())
                .map(PermitNotificationReviewDecision::getDetails)
                .filter(PermitNotificationCompletedDecisionDetails.class::isInstance)
                .map(PermitNotificationCompletedDecisionDetails.class::cast)
                .map(PermitNotificationCompletedDecisionDetails::getFollowUp)
                .map(FollowUp::getFollowUpResponseRequired)
                .ifPresent(responseRequired -> templateParams.put("followUpRequired", responseRequired));

        return templateParams;
    }

    public Map<String, Object> constructParams(final PermitNotificationApplicationReviewRequestTaskPayload payload) {

        Map<String, Object> templateParams = new java.util.HashMap<>(Map.of(
                "officialNotice", ((PermitNotificationReviewDecisionDetails) payload.getReviewDecision().getDetails()).getOfficialNotice()
        ));

        templateParams.putAll(constructReviewDecisionTypeParamsParams(payload.getReviewDecision().getType()));

        templateParams.put("cessationDate", null);
        templateParams.put("resumptionDate", null);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ROOT);

        Optional.ofNullable(payload.getPermitNotification())
                .filter(CessationNotification.class::isInstance)
                .map(CessationNotification.class::cast)
                .map(CessationNotification::getDateOfNonCompliance)
                .ifPresent(nonCompliancePeriod -> {
                    Optional.ofNullable(nonCompliancePeriod.getStartDateOfNonCompliance())
                            .ifPresent(startDate -> templateParams.put("cessationDate", startDate.format(formatter)));

                    Optional.ofNullable(nonCompliancePeriod.getEndDateOfNonCompliance())
                            .ifPresent(endDate -> templateParams.put("resumptionDate", endDate.format(formatter)));
                });

        templateParams.put("followUpRequired", false);
        Optional.ofNullable(payload.getReviewDecision())
                .map(PermitNotificationReviewDecision::getDetails)
                .filter(PermitNotificationCompletedDecisionDetails.class::isInstance)
                .map(PermitNotificationCompletedDecisionDetails.class::cast)
                .map(PermitNotificationCompletedDecisionDetails::getFollowUp)
                .map(FollowUp::getFollowUpResponseRequired)
                .ifPresent(responseRequired -> templateParams.put("followUpRequired", responseRequired));

        return templateParams;
    }

    private Map<String, Object> constructReviewDecisionTypeParamsParams(final PermitNotificationReviewDecisionType type) {
        return Map.of(
                "isPermanentCessation", type.equals(PermitNotificationReviewDecisionType.PERMANENT_CESSATION),
                "isTemporaryCessation", type.equals(PermitNotificationReviewDecisionType.TEMPORARY_CESSATION),
                "isTreatedAsPermanentCessation", type.equals(PermitNotificationReviewDecisionType.CESSATION_TREATED_AS_PERMANENT),
                "isNotCessation", type.equals(PermitNotificationReviewDecisionType.NOT_CESSATION)

        );
    }
}

