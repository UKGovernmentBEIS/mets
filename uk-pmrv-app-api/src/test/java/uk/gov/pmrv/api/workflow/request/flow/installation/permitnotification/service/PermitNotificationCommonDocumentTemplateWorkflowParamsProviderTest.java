package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.*;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PermitNotificationCommonDocumentTemplateWorkflowParamsProviderTest {

    private final PermitNotificationCommonDocumentTemplateWorkflowParamsProvider provider =
            new PermitNotificationCommonDocumentTemplateWorkflowParamsProvider();

    @Test
    void constructParams_withPermanentCessation() {
        PermitNotificationRequestPayload payload = buildBasicPayload(
                "Official Notice Text",
                PermitNotificationReviewDecisionType.PERMANENT_CESSATION
        );

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("officialNotice", "Official Notice Text");
        assertThat(result).containsEntry("isPermanentCessation", true);
        assertThat(result).containsEntry("isTemporaryCessation", false);
        assertThat(result).containsEntry("isTreatedAsPermanentCessation", false);
        assertThat(result).containsEntry("isNotCessation", false);
    }

    @Test
    void constructParams_withTemporaryCessation() {
        PermitNotificationRequestPayload payload = buildBasicPayload(
                "Temp Notice",
                PermitNotificationReviewDecisionType.TEMPORARY_CESSATION
        );

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("officialNotice", "Temp Notice");
        assertThat(result).containsEntry("isTemporaryCessation", true);
    }

    @Test
    void constructParams_withCessationNotificationDates() {
        CessationNotification cessationNotification = new CessationNotification();
        DateOfNonCompliance nonComplianceDate = new DateOfNonCompliance();
        nonComplianceDate.setStartDateOfNonCompliance(LocalDate.of(2023, 1, 15));
        nonComplianceDate.setEndDateOfNonCompliance(LocalDate.of(2023, 3, 1));
        cessationNotification.setDateOfNonCompliance(nonComplianceDate);

        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice("Notice");

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(PermitNotificationReviewDecisionType.PERMANENT_CESSATION);

        PermitNotificationRequestPayload payload = new PermitNotificationRequestPayload();
        payload.setReviewDecision(decision);
        payload.setPermitNotification(cessationNotification);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("cessationDate", "15 Jan 2023");
        assertThat(result).containsEntry("resumptionDate", "1 Mar 2023");
    }

    @Test
    void constructParams_withNullResumptionDate() {
        CessationNotification cessationNotification = new CessationNotification();
        DateOfNonCompliance nonComplianceDate = new DateOfNonCompliance();
        nonComplianceDate.setStartDateOfNonCompliance(LocalDate.of(2023, 4, 10));
        nonComplianceDate.setEndDateOfNonCompliance(null);
        cessationNotification.setDateOfNonCompliance(nonComplianceDate);

        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice("Another Notice");

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(PermitNotificationReviewDecisionType.TEMPORARY_CESSATION);

        PermitNotificationRequestPayload payload = new PermitNotificationRequestPayload();
        payload.setReviewDecision(decision);
        payload.setPermitNotification(cessationNotification);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("cessationDate", "10 Apr 2023");
        assertThat(result).containsEntry("resumptionDate", null);
    }

    @Test
    void constructParams_withFollowUpRequired() {
        FollowUp followUp = new FollowUp();
        followUp.setFollowUpResponseRequired(true);

        PermitNotificationCompletedDecisionDetails completedDetails = new PermitNotificationCompletedDecisionDetails();
        completedDetails.setOfficialNotice("Completed Notice");
        completedDetails.setFollowUp(followUp);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(completedDetails);
        decision.setType(PermitNotificationReviewDecisionType.NOT_CESSATION);

        PermitNotificationRequestPayload payload = new PermitNotificationRequestPayload();
        payload.setReviewDecision(decision);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("followUpRequired", true);
    }

    @Test
    void constructParams_fromTaskPayload_withPermanentCessation() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = buildBasicTaskPayload(
                "Official Notice Text",
                PermitNotificationReviewDecisionType.PERMANENT_CESSATION
        );

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("officialNotice", "Official Notice Text");
        assertThat(result).containsEntry("isPermanentCessation", true);
        assertThat(result).containsEntry("isTemporaryCessation", false);
        assertThat(result).containsEntry("isTreatedAsPermanentCessation", false);
        assertThat(result).containsEntry("isNotCessation", false);
    }

    @Test
    void constructParams_fromTaskPayload_withTemporaryCessation() {
        PermitNotificationApplicationReviewRequestTaskPayload payload = buildBasicTaskPayload(
                "Temp Notice",
                PermitNotificationReviewDecisionType.TEMPORARY_CESSATION
        );

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("officialNotice", "Temp Notice");
        assertThat(result).containsEntry("isTemporaryCessation", true);
    }

    @Test
    void constructParams_fromTaskPayload_withCessationNotificationDates() {
        CessationNotification cessationNotification = new CessationNotification();
        DateOfNonCompliance nonComplianceDate = new DateOfNonCompliance();
        nonComplianceDate.setStartDateOfNonCompliance(LocalDate.of(2023, 1, 15));
        nonComplianceDate.setEndDateOfNonCompliance(LocalDate.of(2023, 3, 1));
        cessationNotification.setDateOfNonCompliance(nonComplianceDate);

        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice("Notice");

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(PermitNotificationReviewDecisionType.PERMANENT_CESSATION);

        PermitNotificationApplicationReviewRequestTaskPayload payload = buildBasicTaskPayload(
                "Temp Notice",
                PermitNotificationReviewDecisionType.PERMANENT_CESSATION
        );
        payload.setReviewDecision(decision);
        payload.setPermitNotification(cessationNotification);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("cessationDate", "15 Jan 2023");
        assertThat(result).containsEntry("resumptionDate", "1 Mar 2023");
    }

    @Test
    void constructParams_fromTaskPayload_withNullResumptionDate() {
        CessationNotification cessationNotification = new CessationNotification();
        DateOfNonCompliance nonComplianceDate = new DateOfNonCompliance();
        nonComplianceDate.setStartDateOfNonCompliance(LocalDate.of(2023, 4, 10));
        nonComplianceDate.setEndDateOfNonCompliance(null);
        cessationNotification.setDateOfNonCompliance(nonComplianceDate);

        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice("Another Notice");

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(PermitNotificationReviewDecisionType.TEMPORARY_CESSATION);


        PermitNotificationApplicationReviewRequestTaskPayload payload = buildBasicTaskPayload(
                "Temp Notice",
                PermitNotificationReviewDecisionType.TEMPORARY_CESSATION
        );

        payload.setReviewDecision(decision);
        payload.setPermitNotification(cessationNotification);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("cessationDate", "10 Apr 2023");
        assertThat(result).containsEntry("resumptionDate", null);
    }

    @Test
    void constructParams_fromTaskPayload_withFollowUpRequired() {
        FollowUp followUp = new FollowUp();
        followUp.setFollowUpResponseRequired(true);

        PermitNotificationCompletedDecisionDetails completedDetails = new PermitNotificationCompletedDecisionDetails();
        completedDetails.setOfficialNotice("Completed Notice");
        completedDetails.setFollowUp(followUp);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(completedDetails);
        decision.setType(PermitNotificationReviewDecisionType.NOT_CESSATION);


        PermitNotificationApplicationReviewRequestTaskPayload payload = buildBasicTaskPayload(
                "Temp Notice",
                PermitNotificationReviewDecisionType.NOT_CESSATION
        );

        payload.setReviewDecision(decision);

        Map<String, Object> result = provider.constructParams(payload);

        assertThat(result).containsEntry("followUpRequired", true);
    }




    // Utility method
    private PermitNotificationRequestPayload buildBasicPayload(String notice, PermitNotificationReviewDecisionType type) {
        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice(notice);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(type);

        PermitNotificationRequestPayload payload = new PermitNotificationRequestPayload();
        payload.setReviewDecision(decision);

        return payload;
    }

    // Utility method
    private PermitNotificationApplicationReviewRequestTaskPayload buildBasicTaskPayload(String notice, PermitNotificationReviewDecisionType type) {
        PermitNotificationReviewDecisionDetails details = new PermitNotificationReviewDecisionDetails();
        details.setOfficialNotice(notice);

        PermitNotificationReviewDecision decision = new PermitNotificationReviewDecision();
        decision.setDetails(details);
        decision.setType(type);

        PermitNotificationApplicationReviewRequestTaskPayload payload =
                PermitNotificationApplicationReviewRequestTaskPayload
                        .builder()
                        .reviewDecision(decision)
                        .build();

        payload.setReviewDecision(decision);

        return payload;
    }
}
