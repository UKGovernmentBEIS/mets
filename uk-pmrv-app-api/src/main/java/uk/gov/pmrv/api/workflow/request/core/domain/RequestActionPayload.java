package uk.gov.pmrv.api.workflow.request.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.verify.domain.AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.common.domain.AviationAerUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.review.domain.AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.ukets.verify.domain.AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aviationaccountclosure.domain.AviationAccountClosureSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.common.domain.AviationDreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.submit.domain.EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.submit.domain.EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.amendsubmit.domain.EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submit.domain.EmpVariationCorsiaApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.amendsubmit.domain.EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submit.domain.EmpVariationUkEtsApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.vir.domain.AviationVirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.BatchReissueSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.reissue.domain.ReissueCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.accountinstallationopening.domain.InstallationAccountOpeningDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationAmendsSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationVerificationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationAcceptedWithCorrectionsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationAcceptedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationEndedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationProceededToAuthorityRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationDeterminedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferAApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.amendsubmit.domain.PermitVariationApplicationReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submit.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationRegulatorLedApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationRespondedToRegulatorCommentsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationReviewedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationClosedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentCancelledRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentProcessedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeDecisionForcedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiResponseSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Schema(
        discriminatorMapping = {
                @DiscriminatorMapping(schema = InstallationAccountOpeningApplicationSubmittedRequestActionPayload.class, value = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = InstallationAccountOpeningApprovedRequestActionPayload.class, value = "INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = InstallationAccountOpeningDecisionRequestActionPayload.class, value = "INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD"),

                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_ISSUANCE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationGrantedRequestActionPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationRejectedRequestActionPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationSubmittedRequestActionPayload.class, value = "PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_SURRENDER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.class, value = "PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationGrantedRequestActionPayload.class, value = "PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationRejectedRequestActionPayload.class, value = "PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitSurrenderApplicationSubmittedRequestActionPayload.class, value = "PERMIT_SURRENDER_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitCessationCompletedRequestActionPayload.class, value = "PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD"),

                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_NOTIFICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationApplicationSubmittedRequestActionPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.class, value = "PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpResponseSubmittedRequestActionPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.class, value = "PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_REVOCATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitCessationCompletedRequestActionPayload.class, value = "PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationApplicationSubmittedRequestActionPayload.class, value = "PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitRevocationApplicationWithdrawnRequestActionPayload.class, value = "PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitVariationApplicationSubmittedRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationGrantedRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationRejectedRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationDeemedWithdrawnRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_VARIATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_VARIATION_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationReturnedForAmendsRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.class, value = "PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),

                @DiscriminatorMapping(schema = PermitTransferAApplicationSubmittedRequestActionPayload.class, value = "PERMIT_TRANSFER_A_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferAApplicationDeterminedRequestActionPayload.class, value = "PERMIT_TRANSFER_A_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferAApplicationDeterminedRequestActionPayload.class, value = "PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferAApplicationDeterminedRequestActionPayload.class, value = "PERMIT_TRANSFER_A_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationSubmittedRequestActionPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitTransferBApplicationGrantedRequestActionPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_GRANTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationRejectedRequestActionPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "PERMIT_TRANSFER_B_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.class, value = "PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                
                @DiscriminatorMapping(schema = BatchReissueSubmittedRequestActionPayload.class, value = "PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = BatchReissueCompletedRequestActionPayload.class, value = "PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = ReissueCompletedRequestActionPayload.class, value = "PERMIT_REISSUE_COMPLETED_PAYLOAD"),

                @DiscriminatorMapping(schema = AerApplicationSubmittedRequestActionPayload.class, value = "AER_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationAmendsSubmittedRequestActionPayload.class, value = "AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationCompletedRequestActionPayload.class, value = "AER_APPLICATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationVerificationSubmittedRequestActionPayload.class, value = "AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AerApplicationReturnedForAmendsRequestActionPayload.class, value = "AER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = DreApplicationSubmittedRequestActionPayload.class, value = "DRE_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "DRE_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = VirApplicationSubmittedRequestActionPayload.class, value = "VIR_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = VirApplicationReviewedRequestActionPayload.class, value = "VIR_APPLICATION_REVIEWED_PAYLOAD"),
                @DiscriminatorMapping(schema = VirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, value = "VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),

                @DiscriminatorMapping(schema = AirApplicationSubmittedRequestActionPayload.class, value = "AIR_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AirApplicationReviewedRequestActionPayload.class, value = "AIR_APPLICATION_REVIEWED_PAYLOAD"),
                @DiscriminatorMapping(schema = AirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, value = "AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),
            
                @DiscriminatorMapping(schema = NonComplianceApplicationClosedRequestActionPayload.class, value = "NON_COMPLIANCE_APPLICATION_CLOSED_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceApplicationSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload.class, value = "NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = NerApplicationSubmittedRequestActionPayload.class, value = "NER_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "NER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationReturnedForAmendsRequestActionPayload.class, value = "NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationEndedRequestActionPayload.class, value = "NER_APPLICATION_ENDED_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationProceededToAuthorityRequestActionPayload.class, value = "NER_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationAcceptedRequestActionPayload.class, value = "NER_APPLICATION_ACCEPTED_PAYLOAD"),
                @DiscriminatorMapping(schema = NerApplicationRejectedRequestActionPayload.class, value = "NER_APPLICATION_REJECTED_PAYLOAD"),

                @DiscriminatorMapping(schema = DoalApplicationProceededToAuthorityRequestActionPayload.class, value = "DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "DOAL_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationClosedRequestActionPayload.class, value = "DOAL_APPLICATION_CLOSED_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationAcceptedRequestActionPayload.class, value = "DOAL_APPLICATION_ACCEPTED_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationAcceptedWithCorrectionsRequestActionPayload.class, value = "DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD"),
                @DiscriminatorMapping(schema = DoalApplicationRejectedRequestActionPayload.class, value = "DOAL_APPLICATION_REJECTED_PAYLOAD"),

                @DiscriminatorMapping(schema = RfiResponseSubmittedRequestActionPayload.class, value = "RFI_RESPONSE_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = RfiSubmittedRequestActionPayload.class, value = "RFI_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = RdeDecisionForcedRequestActionPayload.class, value = "RDE_DECISION_FORCED_PAYLOAD"),
                @DiscriminatorMapping(schema = RdeRejectedRequestActionPayload.class, value = "RDE_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = RdeSubmittedRequestActionPayload.class, value = "RDE_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_MARKED_AS_PAID_PAYLOAD"),
                @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_MARKED_AS_RECEIVED_PAYLOAD"),
                @DiscriminatorMapping(schema = PaymentProcessedRequestActionPayload.class, value = "PAYMENT_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = PaymentCancelledRequestActionPayload.class, value = "PAYMENT_CANCELLED_PAYLOAD"),

                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationSubmittedRequestActionPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = WithholdingOfAllowancesApplicationClosedRequestActionPayload.class, value = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED_PAYLOAD"),

                @DiscriminatorMapping(schema = ReturnOfAllowancesApplicationSubmittedRequestActionPayload.class, value = "RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload.class, value = "RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "RETURN_OF_ALLOWANCES_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationApprovedRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
                
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationSubmittedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationApprovedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationRejectedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload.class, value = "EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
                
                @DiscriminatorMapping(schema = BatchReissueSubmittedRequestActionPayload.class, value = "EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = BatchReissueCompletedRequestActionPayload.class, value = "EMP_BATCH_REISSUE_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = ReissueCompletedRequestActionPayload.class, value = "EMP_REISSUE_COMPLETED_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAccountClosureSubmittedRequestActionPayload.class, value = "AVIATION_ACCOUNT_CLOSURE_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationSubmittedRequestActionPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationSubmittedRequestActionPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationCompletedRequestActionPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload.class, value = "AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationDreApplicationSubmittedRequestActionPayload.class, value = "AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationVirApplicationSubmittedRequestActionPayload.class, value = "AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationVirApplicationReviewedRequestActionPayload.class, value = "AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, value = "AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationApprovedRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload.class, value = "EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationSubmittedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),                
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationApprovedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationRejectedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_REJECTED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),
                @DiscriminatorMapping(schema = PeerReviewDecisionSubmittedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload.class, value = "EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),

                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationSubmittedRequestActionPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationSubmittedRequestActionPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationCompletedRequestActionPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD"),
                @DiscriminatorMapping(schema = AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload.class, value = "AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),

        },
        discriminatorProperty = "payloadType")

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME , include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "payloadType", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InstallationAccountOpeningApplicationSubmittedRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = InstallationAccountOpeningApprovedRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = InstallationAccountOpeningDecisionRequestActionPayload.class, name = "INSTALLATION_ACCOUNT_OPENING_DECISION_PAYLOAD"),

        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_ISSUANCE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationGrantedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationRejectedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationSubmittedRequestActionPayload.class, name = "PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_SURRENDER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationGrantedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationRejectedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitSurrenderApplicationSubmittedRequestActionPayload.class, name = "PERMIT_SURRENDER_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitCessationCompletedRequestActionPayload.class, name = "PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD"),

        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload.class, name = "PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpResponseSubmittedRequestActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.class, name = "PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD"),

        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_REVOCATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitCessationCompletedRequestActionPayload.class, name = "PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitRevocationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitRevocationApplicationWithdrawnRequestActionPayload.class, name = "PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD"),

        @JsonSubTypes.Type(value = PermitVariationApplicationSubmittedRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitVariationApplicationGrantedRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitVariationApplicationRejectedRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitVariationApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_VARIATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_VARIATION_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitVariationApplicationReturnedForAmendsRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitVariationApplicationRegulatorLedApprovedRequestActionPayload.class, name = "PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),

        @JsonSubTypes.Type(value = PermitTransferAApplicationSubmittedRequestActionPayload.class, name = "PERMIT_TRANSFER_A_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitTransferAApplicationDeterminedRequestActionPayload.class, name = "PERMIT_TRANSFER_A_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitTransferAApplicationDeterminedRequestActionPayload.class, name = "PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitTransferAApplicationDeterminedRequestActionPayload.class, name = "PERMIT_TRANSFER_A_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitTransferBApplicationSubmittedRequestActionPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationDeemedWithdrawnRequestActionPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitTransferBApplicationGrantedRequestActionPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_GRANTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationRejectedRequestActionPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "PERMIT_TRANSFER_B_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PermitIssuanceApplicationReturnedForAmendsRequestActionPayload.class, name = "PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        
        @JsonSubTypes.Type(value = BatchReissueSubmittedRequestActionPayload.class, name = "PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = BatchReissueCompletedRequestActionPayload.class, name = "PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = ReissueCompletedRequestActionPayload.class, name = "PERMIT_REISSUE_COMPLETED_PAYLOAD"),

        @JsonSubTypes.Type(value = AerApplicationSubmittedRequestActionPayload.class, name = "AER_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AerApplicationAmendsSubmittedRequestActionPayload.class, name = "AER_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AerApplicationCompletedRequestActionPayload.class, name = "AER_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = AerApplicationVerificationSubmittedRequestActionPayload.class, name = "AER_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AerApplicationReturnedForAmendsRequestActionPayload.class, name = "AER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),

        @JsonSubTypes.Type(value = DreApplicationSubmittedRequestActionPayload.class, name = "DRE_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "DRE_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = VirApplicationSubmittedRequestActionPayload.class, name = "VIR_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = VirApplicationReviewedRequestActionPayload.class, name = "VIR_APPLICATION_REVIEWED_PAYLOAD"),
        @JsonSubTypes.Type(value = VirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, name = "VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),

        @JsonSubTypes.Type(value = AirApplicationSubmittedRequestActionPayload.class, name = "AIR_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AirApplicationReviewedRequestActionPayload.class, name = "AIR_APPLICATION_REVIEWED_PAYLOAD"),
        @JsonSubTypes.Type(value = AirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, name = "AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),
    
        @JsonSubTypes.Type(value = NonComplianceApplicationClosedRequestActionPayload.class, name = "NON_COMPLIANCE_APPLICATION_CLOSED_PAYLOAD"),
        @JsonSubTypes.Type(value = NonComplianceApplicationSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NonComplianceDailyPenaltyNoticeApplicationSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NonComplianceNoticeOfIntentApplicationSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NonComplianceCivilPenaltyApplicationSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NonComplianceFinalDeterminationApplicationSubmittedRequestActionPayload.class, name = "NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = NerApplicationSubmittedRequestActionPayload.class, name = "NER_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "NER_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NerApplicationReturnedForAmendsRequestActionPayload.class, name = "NER_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = NerApplicationEndedRequestActionPayload.class, name = "NER_APPLICATION_ENDED_PAYLOAD"),
        @JsonSubTypes.Type(value = NerApplicationProceededToAuthorityRequestActionPayload.class, name = "NER_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD"),
        @JsonSubTypes.Type(value = NerApplicationAcceptedRequestActionPayload.class, name = "NER_APPLICATION_ACCEPTED_PAYLOAD"),
        @JsonSubTypes.Type(value = NerApplicationRejectedRequestActionPayload.class, name = "NER_APPLICATION_REJECTED_PAYLOAD"),

        @JsonSubTypes.Type(value = DoalApplicationProceededToAuthorityRequestActionPayload.class, name = "DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "DOAL_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = DoalApplicationClosedRequestActionPayload.class, name = "DOAL_APPLICATION_CLOSED_PAYLOAD"),
        @JsonSubTypes.Type(value = DoalApplicationAcceptedRequestActionPayload.class, name = "DOAL_APPLICATION_ACCEPTED_PAYLOAD"),
        @JsonSubTypes.Type(value = DoalApplicationAcceptedWithCorrectionsRequestActionPayload.class, name = "DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS_PAYLOAD"),
        @JsonSubTypes.Type(value = DoalApplicationRejectedRequestActionPayload.class, name = "DOAL_APPLICATION_REJECTED_PAYLOAD"),
    
        @JsonSubTypes.Type(value = RfiResponseSubmittedRequestActionPayload.class, name = "RFI_RESPONSE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = RfiSubmittedRequestActionPayload.class, name = "RFI_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = RdeDecisionForcedRequestActionPayload.class, name = "RDE_DECISION_FORCED_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeRejectedRequestActionPayload.class, name = "RDE_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = RdeSubmittedRequestActionPayload.class, name = "RDE_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_PAID_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_MARKED_AS_RECEIVED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentProcessedRequestActionPayload.class, name = "PAYMENT_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PaymentCancelledRequestActionPayload.class, name = "PAYMENT_CANCELLED_PAYLOAD"),

        @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationSubmittedRequestActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationWithdrawnRequestActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = WithholdingOfAllowancesApplicationClosedRequestActionPayload.class, name = "WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED_PAYLOAD"),

        @JsonSubTypes.Type(value = ReturnOfAllowancesApplicationSubmittedRequestActionPayload.class, name = "RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = ReturnOfAllowancesReturnedApplicationCompletedRequestActionPayload.class, name = "RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "RETURN_OF_ALLOWANCES_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationApprovedRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationDeemedWithdrawnRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationReturnedForAmendsRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceUkEtsApplicationAmendsSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationSubmittedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationApprovedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationRegulatorLedApprovedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationDeemedWithdrawnRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationRejectedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationReturnedForAmendsRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationUkEtsApplicationAmendsSubmittedRequestActionPayload.class, name = "EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
        
        @JsonSubTypes.Type(value = BatchReissueSubmittedRequestActionPayload.class, name = "EMP_BATCH_REISSUE_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = BatchReissueCompletedRequestActionPayload.class, name = "EMP_BATCH_REISSUE_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = ReissueCompletedRequestActionPayload.class, name = "EMP_REISSUE_COMPLETED_PAYLOAD"),

        @JsonSubTypes.Type(value = AviationAccountClosureSubmittedRequestActionPayload.class, name = "AVIATION_ACCOUNT_CLOSURE_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = AviationAerUkEtsApplicationSubmittedRequestActionPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerUkEtsApplicationSubmittedRequestActionPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerUkEtsApplicationVerificationSubmittedRequestActionPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerUkEtsApplicationCompletedRequestActionPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerUkEtsApplicationReturnedForAmendsRequestActionPayload.class, name = "AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),

        @JsonSubTypes.Type(value = AviationDreApplicationSubmittedRequestActionPayload.class, name = "AVIATION_DRE_UKETS_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = AviationVirApplicationSubmittedRequestActionPayload.class, name = "AVIATION_VIR_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationVirApplicationReviewedRequestActionPayload.class, name = "AVIATION_VIR_APPLICATION_REVIEWED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationVirApplicationRespondedToRegulatorCommentsRequestActionPayload.class, name = "AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD"),

        @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationApprovedRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationDeemedWithdrawnRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationReturnedForAmendsRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpIssuanceCorsiaApplicationAmendsSubmittedRequestActionPayload.class, name = "EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationSubmittedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),        
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationApprovedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationDeemedWithdrawnRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationRejectedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_REJECTED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationReturnedForAmendsRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationRegulatorLedApprovedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED_PAYLOAD"),
        @JsonSubTypes.Type(value = PeerReviewDecisionSubmittedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_PEER_REVIEW_DECISION_REGULATOR_LED_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = EmpVariationCorsiaApplicationAmendsSubmittedRequestActionPayload.class, name = "EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),

        @JsonSubTypes.Type(value = AviationAerCorsiaApplicationSubmittedRequestActionPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerCorsiaApplicationSubmittedRequestActionPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerCorsiaApplicationVerificationSubmittedRequestActionPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerCorsiaApplicationCompletedRequestActionPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_COMPLETED_PAYLOAD"),
        @JsonSubTypes.Type(value = AviationAerCorsiaApplicationReturnedForAmendsRequestActionPayload.class, name = "AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS_PAYLOAD")
})
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class RequestActionPayload {

    private RequestActionPayloadType payloadType;

    @JsonIgnore
    public Map<UUID, String> getAttachments() {
        return Collections.emptyMap();
    }

    @JsonIgnore
    public Map<UUID, String> getFileDocuments() {
        return Collections.emptyMap();
    }

}
