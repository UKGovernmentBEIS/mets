package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.APPLICATION_VERIFICATION_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.CONFIRM_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.MAKE_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RDE_RESPONSE_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RFI_RESPONSE_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.TRACK_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE;

/**
 * Task type enum. <br/>
 * Note: The enum is used in bpmn workflow engine to set the user task definition key (ID), e.g. <br/>
 * <i>&lt;bpmn:userTask id="INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW" name="Review application"&gt;</i>
 *
 */
@Getter
public enum RequestTaskType {
    /*
     ************************************************* INSTALLATION *************************************************
     */

    /**
     * INSTALLATION_ACCOUNT_OPENING
     */
    INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW(true, RequestType.INSTALLATION_ACCOUNT_OPENING) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION,
                RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION);
        }
    },

    INSTALLATION_ACCOUNT_OPENING_ARCHIVE(true, RequestType.INSTALLATION_ACCOUNT_OPENING) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE);
        }
    },

    INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE(true, RequestType.INSTALLATION_ACCOUNT_OPENING) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE);
        }
    },

    /**
     * PERMIT_ISSUANCE
     */
    PERMIT_ISSUANCE_APPLICATION_SUBMIT(true, RequestType.PERMIT_ISSUANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION);
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_REVIEW(true, RequestType.PERMIT_ISSUANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_ISSUANCE_APPLICATION_REVIEW(true, RequestType.PERMIT_ISSUANCE, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW,
                    RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION,
                    RequestTaskActionType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW,
                    RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_AMENDS(true, RequestType.PERMIT_ISSUANCE, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_ISSUANCE_RECALL_FROM_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT(true, RequestType.PERMIT_ISSUANCE){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND);
        }
    },

    PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_ISSUANCE, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_ISSUANCE, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT(false, RequestType.PERMIT_ISSUANCE, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE(false, RequestType.PERMIT_ISSUANCE, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT(false, RequestType.PERMIT_ISSUANCE, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE(false, RequestType.PERMIT_ISSUANCE, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                           RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    PERMIT_ISSUANCE_MAKE_PAYMENT(false, RequestType.PERMIT_ISSUANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_ISSUANCE_TRACK_PAYMENT(false, RequestType.PERMIT_ISSUANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_ISSUANCE_CONFIRM_PAYMENT(false, RequestType.PERMIT_ISSUANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * PERMIT_SURRENDER
     */
    PERMIT_SURRENDER_APPLICATION_SUBMIT(true, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_SURRENDER_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_SURRENDER_SUBMIT_APPLICATION,
                RequestTaskActionType.PERMIT_SURRENDER_CANCEL_APPLICATION);
        }
    },

    PERMIT_SURRENDER_APPLICATION_REVIEW(true, RequestType.PERMIT_SURRENDER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.PERMIT_SURRENDER_REQUEST_PEER_REVIEW,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_REVIEW(true, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_SURRENDER_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_SURRENDER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_SURRENDER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT(false, RequestType.PERMIT_SURRENDER, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE(false, RequestType.PERMIT_SURRENDER, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT(false, RequestType.PERMIT_SURRENDER, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE(false, RequestType.PERMIT_SURRENDER, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                    RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    PERMIT_SURRENDER_CESSATION_SUBMIT(true, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_CESSATION,
                RequestTaskActionType.PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION
            );
        }
    },

    PERMIT_SURRENDER_MAKE_PAYMENT(false, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_SURRENDER_TRACK_PAYMENT(false, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_SURRENDER_CONFIRM_PAYMENT(false, RequestType.PERMIT_SURRENDER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * PERMIT_REVOCATION
     */
    PERMIT_REVOCATION_APPLICATION_SUBMIT(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_SAVE_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION,
                           RequestTaskActionType.PERMIT_REVOCATION_CANCEL_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_REQUEST_PEER_REVIEW);
        }
    },

    PERMIT_REVOCATION_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_CANCEL_APPLICATION);
        }
    },

    PERMIT_REVOCATION_WAIT_FOR_APPEAL(true, RequestType.PERMIT_REVOCATION){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_WITHDRAW_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_UPLOAD_ATTACHMENT,
                           RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL);
        }
    },

    PERMIT_REVOCATION_CESSATION_SUBMIT(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_REVOCATION_SAVE_CESSATION,
                RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION
            );
        }
    },

    PERMIT_REVOCATION_MAKE_PAYMENT(true, RequestType.PERMIT_REVOCATION, RequestExpirationType.PAYMENT) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_REVOCATION_TRACK_PAYMENT(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_REVOCATION_CONFIRM_PAYMENT(true, RequestType.PERMIT_REVOCATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * PERMIT_NOTIFICATION
     */
    PERMIT_NOTIFICATION_APPLICATION_SUBMIT(true, RequestType.PERMIT_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_APPLICATION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.PERMIT_NOTIFICATION_SUBMIT_APPLICATION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_CANCEL_APPLICATION
            );
        }
    },

    PERMIT_NOTIFICATION_APPLICATION_REVIEW(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_REVIEW(true, RequestType.PERMIT_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT(false, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE(false, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.FOLLOW_UP_RESPONSE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.FOLLOW_UP_RESPONSE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE);
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW(true, RequestType.PERMIT_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW(true, RequestType.PERMIT_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS
            );
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.FOLLOW_UP_RESPONSE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS
            );
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT(true, RequestType.PERMIT_NOTIFICATION, RequestExpirationType.FOLLOW_UP_RESPONSE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND
            );
        }
    },

    /**
     * PERMIT_VARIATION_REGUALTOR_LED
     */
    PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT(true, RequestType.PERMIT_VARIATION){
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
    		return List.of(
    				RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
    				RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED,
    				RequestTaskActionType.PERMIT_VARIATION_CANCEL_APPLICATION
    				);
        }
    },

    PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED
            );
        }
    },

    PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(

            );
        }
    },

    PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * PERMIT_VARIATION_OPERATOR_LED
     */
    PERMIT_VARIATION_APPLICATION_SUBMIT(true, RequestType.PERMIT_VARIATION){
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
			return List.of(
					RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION,
					RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_SUBMIT_APPLICATION,
                    RequestTaskActionType.PERMIT_VARIATION_CANCEL_APPLICATION);
        }
    },

    PERMIT_VARIATION_APPLICATION_REVIEW(true, RequestType.PERMIT_VARIATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW,
            		RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION,
                    RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION,
            		RequestTaskActionType.RFI_SUBMIT,
            		RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
            		RequestTaskActionType.RDE_SUBMIT,
                    RequestTaskActionType.PERMIT_VARIATION_REQUEST_PEER_REVIEW,
                    RequestTaskActionType.PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS
            		);
        }
    },

    PERMIT_VARIATION_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_VARIATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_VARIATION, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_VARIATION_WAIT_FOR_REVIEW(true, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_VARIATION_RFI_RESPONSE_SUBMIT(false, RequestType.PERMIT_VARIATION, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE(false, RequestType.PERMIT_VARIATION, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_VARIATION_RDE_RESPONSE_SUBMIT(false, RequestType.PERMIT_VARIATION, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE(false, RequestType.PERMIT_VARIATION, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                    RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    PERMIT_VARIATION_MAKE_PAYMENT(false, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_VARIATION_TRACK_PAYMENT(false, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_VARIATION_CONFIRM_PAYMENT(false, RequestType.PERMIT_VARIATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT(true, RequestType.PERMIT_VARIATION){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND);
        }
    },

    PERMIT_VARIATION_WAIT_FOR_AMENDS(true, RequestType.PERMIT_VARIATION, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_VARIATION_RECALL_FROM_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    /**
     * PERMIT_TRANSFER_A
     */
    PERMIT_TRANSFER_A_APPLICATION_SUBMIT(true, RequestType.PERMIT_TRANSFER_A) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_TRANSFER_A_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_TRANSFER_A_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_TRANSFER_A_SUBMIT_APPLICATION,
                RequestTaskActionType.PERMIT_TRANSFER_CANCEL_APPLICATION
            );
        }
    },

    PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER(true, RequestType.PERMIT_TRANSFER_A) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_TRANSFER_A_MAKE_PAYMENT(false, RequestType.PERMIT_TRANSFER_A) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_TRANSFER_A_TRACK_PAYMENT(true, RequestType.PERMIT_TRANSFER_A) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_TRANSFER_A_CONFIRM_PAYMENT(true, RequestType.PERMIT_TRANSFER_A) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * PERMIT_TRANSFER_B
     */
    PERMIT_TRANSFER_B_APPLICATION_SUBMIT(true, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_TRANSFER_CANCEL_APPLICATION,
                RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_TRANSFER_B_SUBMIT_APPLICATION
            );
        }
    },

    PERMIT_TRANSFER_B_WAIT_FOR_REVIEW(true, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_TRANSFER_B_APPLICATION_REVIEW(true, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION,
                RequestTaskActionType.PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT,
                RequestTaskActionType.PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW,
                RequestTaskActionType.PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS
            );
        }
    },

    PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW(true, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_TRANSFER_B_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW(true, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_TRANSFER_B_MAKE_PAYMENT(false, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_TRANSFER_B_TRACK_PAYMENT(false, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_TRANSFER_B_CONFIRM_PAYMENT(false, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT(false, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE(false, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RFI_CANCEL);
        }
    },

    PERMIT_TRANSFER_B_RDE_RESPONSE_SUBMIT(false, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE(false, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_FORCE_DECISION,
                RequestTaskActionType.RDE_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_TRANSFER_B_WAIT_FOR_AMENDS(true, RequestType.PERMIT_TRANSFER_B, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_TRANSFER_B_RECALL_FROM_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT(true, RequestType.PERMIT_TRANSFER_B) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_TRANSFER_B_SUBMIT_APPLICATION_AMEND
            );
        }
    },

    /**
     * NON_COMPLIANCE
     */
    NON_COMPLIANCE_APPLICATION_SUBMIT(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_SUBMIT_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_DAILY_PENALTY_NOTICE(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW
            );
        }
    },

    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_NOTICE_OF_INTENT(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW
            );
        }
    },

    NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_CIVIL_PENALTY(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    NON_COMPLIANCE_FINAL_DETERMINATION(true, RequestType.NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SUBMIT_APPLICATION
            );
        }
    },

    /**
     * NER
     */
    NER_APPLICATION_SUBMIT(true, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NER_CANCEL_APPLICATION,
                RequestTaskActionType.NER_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NER_SAVE_APPLICATION,
                RequestTaskActionType.NER_SUBMIT_APPLICATION
            );
        }
    },

    NER_APPLICATION_REVIEW(true, RequestType.NER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NER_SAVE_APPLICATION_REVIEW,
                RequestTaskActionType.NER_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.NER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.NER_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.NER_REQUEST_PEER_REVIEW,
                RequestTaskActionType.NER_REVIEW_RETURN_FOR_AMENDS,
                RequestTaskActionType.NER_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.NER_COMPLETE_REVIEW
            );
        }
    },

    NER_WAIT_FOR_REVIEW(true, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    NER_APPLICATION_PEER_REVIEW(true, RequestType.NER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NER_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NER_UPLOAD_ATTACHMENT
            );
        }
    },

    NER_WAIT_FOR_PEER_REVIEW(true, RequestType.NER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    NER_WAIT_FOR_AMENDS(true, RequestType.NER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    NER_APPLICATION_AMENDS_SUBMIT(true, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NER_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.NER_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NER_SUBMIT_APPLICATION_AMEND);
        }
    },

    NER_MAKE_PAYMENT(false, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    NER_TRACK_PAYMENT(false, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    NER_CONFIRM_PAYMENT(false, RequestType.NER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },
    
    NER_AUTHORITY_RESPONSE(true, RequestType.NER, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NER_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NER_SAVE_AUTHORITY_RESPONSE,
                RequestTaskActionType.NER_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION
            );
        }
    },

    /**
     * DOAL
     */
    DOAL_APPLICATION_SUBMIT(true, RequestType.DOAL) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.DOAL_SAVE_APPLICATION,
                    RequestTaskActionType.DOAL_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.DOAL_PROCEED_TO_AUTHORITY_APPLICATION,
                    RequestTaskActionType.DOAL_CLOSE_APPLICATION,
                    RequestTaskActionType.DOAL_CANCEL_APPLICATION,
                    RequestTaskActionType.DOAL_REQUEST_PEER_REVIEW
            );
        }
    },

    DOAL_APPLICATION_PEER_REVIEW(true, RequestType.DOAL) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.DOAL_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    DOAL_WAIT_FOR_PEER_REVIEW(true, RequestType.DOAL) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.DOAL_CANCEL_APPLICATION
            );
        }
    },

    DOAL_AUTHORITY_RESPONSE(true, RequestType.DOAL) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.DOAL_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.DOAL_SAVE_AUTHORITY_RESPONSE,
                    RequestTaskActionType.DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION
            );
        }
    },

    /**
     * AER
     */
    AER_APPLICATION_SUBMIT(true, RequestType.AER, RequestExpirationType.AER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AER_SAVE_APPLICATION,
                    RequestTaskActionType.AER_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.AER_REQUEST_VERIFICATION,
                    RequestTaskActionType.AER_SUBMIT_APPLICATION
            );
        }
    },

    AER_APPLICATION_REVIEW(true, RequestType.AER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AER_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.AER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                    RequestTaskActionType.AER_COMPLETE_REVIEW,
                    RequestTaskActionType.AER_REVIEW_RETURN_FOR_AMENDS
            );
        }
    },

    AER_WAIT_FOR_REVIEW(true, RequestType.AER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AER_APPLICATION_AMENDS_SUBMIT(true, RequestType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AER_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.AER_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION,
                RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND);
        }
    },

    AER_WAIT_FOR_AMENDS(true, RequestType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AER_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AER_SAVE_APPLICATION_VERIFICATION,
                    RequestTaskActionType.AER_SUBMIT_APPLICATION_VERIFICATION,
                    RequestTaskActionType.AER_VERIFICATION_UPLOAD_ATTACHMENT
            );
        }
    },

    AER_WAIT_FOR_VERIFICATION(true, RequestType.AER, RequestExpirationType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AER_RECALL_FROM_VERIFICATION);
        }
    },

    AER_AMEND_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AER_SAVE_APPLICATION_VERIFICATION,
                    RequestTaskActionType.AER_SUBMIT_APPLICATION_VERIFICATION,
                    RequestTaskActionType.AER_VERIFICATION_UPLOAD_ATTACHMENT
            );
        }
    },

    AER_AMEND_WAIT_FOR_VERIFICATION(true, RequestType.AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AER_RECALL_FROM_VERIFICATION);
        }
    },

    VIR_APPLICATION_SUBMIT(true, RequestType.VIR, RequestExpirationType.VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.VIR_SAVE_APPLICATION,
                    RequestTaskActionType.VIR_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.VIR_SUBMIT_APPLICATION
            );
        }
    },

    /**
     * VIR
     */
    VIR_APPLICATION_REVIEW(true, RequestType.VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.VIR_SAVE_REVIEW,
                    RequestTaskActionType.VIR_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    VIR_WAIT_FOR_REVIEW(true, RequestType.VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    VIR_RESPOND_TO_REGULATOR_COMMENTS(true, RequestType.VIR, RequestExpirationType.VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS,
                    RequestTaskActionType.VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS
            );
        }
    },

    VIR_RFI_RESPONSE_SUBMIT(false, RequestType.VIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    VIR_WAIT_FOR_RFI_RESPONSE(false, RequestType.VIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    /**
     * AIR
     */
    AIR_APPLICATION_SUBMIT(true, RequestType.AIR, RequestExpirationType.AIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AIR_SAVE_APPLICATION,
                RequestTaskActionType.AIR_UPLOAD_ATTACHMENT,
                RequestTaskActionType.AIR_SUBMIT_APPLICATION
            );
        }
    },

    AIR_APPLICATION_REVIEW(true, RequestType.AIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AIR_SAVE_REVIEW,
                RequestTaskActionType.AIR_REVIEW_UPLOAD_ATTACHMENT,
                RequestTaskActionType.AIR_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    AIR_WAIT_FOR_REVIEW(true, RequestType.AIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AIR_RFI_RESPONSE_SUBMIT(false, RequestType.AIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    AIR_WAIT_FOR_RFI_RESPONSE(false, RequestType.AIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    AIR_RESPOND_TO_REGULATOR_COMMENTS(true, RequestType.AIR, RequestExpirationType.AIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AIR_UPLOAD_ATTACHMENT,
                RequestTaskActionType.AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS,
                RequestTaskActionType.AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS
            );
        }
    },

    /**
     * DRE
     */
    DRE_APPLICATION_SUBMIT(true, RequestType.DRE){
		@Override
		public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
			return List.of(
					RequestTaskActionType.DRE_SAVE_APPLICATION,
					RequestTaskActionType.DRE_APPLY_UPLOAD_ATTACHMENT,
					RequestTaskActionType.DRE_SUBMIT_NOTIFY_OPERATOR,
					RequestTaskActionType.DRE_CANCEL_APPLICATION,
					RequestTaskActionType.DRE_REQUEST_PEER_REVIEW
					);
		}
    },

    DRE_APPLICATION_PEER_REVIEW(true, RequestType.DRE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.DRE_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    DRE_WAIT_FOR_PEER_REVIEW(true, RequestType.DRE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.DRE_CANCEL_APPLICATION);
        }
    },

    DRE_MAKE_PAYMENT(true, RequestType.DRE, RequestExpirationType.PAYMENT) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    DRE_TRACK_PAYMENT(true, RequestType.DRE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    DRE_CONFIRM_PAYMENT(true, RequestType.DRE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * WITHHOLDING_OF_ALLOWANCES
     */
    WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT(true, RequestType.WITHHOLDING_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_SAVE_APPLICATION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW
            );
        }
    },

    WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW(true, RequestType.WITHHOLDING_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_CANCEL_APPLICATION);
        }
    },

    WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW(true, RequestType.WITHHOLDING_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT(true, RequestType.WITHHOLDING_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_SAVE_APPLICATION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_CLOSE_APPLICATION,
                    RequestTaskActionType.WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_UPLOAD_ATTACHMENT
            );
        }
    },

    /**
     * RETURN_OF_ALLOWANCES
     */
    RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT(true, RequestType.RETURN_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_SAVE_APPLICATION,
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_CANCEL_APPLICATION,
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW
            );
        }
    },

    RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW(true, RequestType.RETURN_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW(true, RequestType.RETURN_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RETURN_OF_ALLOWANCES_CANCEL_APPLICATION);
        }
    },

    RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT(true, RequestType.RETURN_OF_ALLOWANCES) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION,
                    RequestTaskActionType.RETURN_OF_ALLOWANCES_RETURNED_SUBMIT_APPLICATION
            );
        }
    },



    /*
     **************************************************** AVIATION ****************************************************
     */

    /**
     * AVIATION_ACCOUNT_CLOSURE
     */
    AVIATION_ACCOUNT_CLOSURE_SUBMIT(true, RequestType.AVIATION_ACCOUNT_CLOSURE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION,
                    RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_SUBMIT_APPLICATION,
                    RequestTaskActionType.AVIATION_ACCOUNT_CLOSURE_CANCEL_APPLICATION
            );
        }
    },


    /**
     * AVIATION NON-COMPLIANCE
     */
    AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_SUBMIT_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW
            );
        }
    },

    AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW
            );
        }
    },

    AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_CIVIL_PENALTY(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR,
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION,
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION
            );
        }
    },

    AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION(true, RequestType.AVIATION_NON_COMPLIANCE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.NON_COMPLIANCE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.NON_COMPLIANCE_CLOSE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SAVE_APPLICATION,
                RequestTaskActionType.NON_COMPLIANCE_FINAL_DETERMINATION_SUBMIT_APPLICATION
            );
        }
    },

    /**
     * AVIATION_VIR
     */
    AVIATION_VIR_APPLICATION_SUBMIT(true, RequestType.AVIATION_VIR, RequestExpirationType.AVIATION_VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_VIR_UPLOAD_ATTACHMENT,
                RequestTaskActionType.AVIATION_VIR_SAVE_APPLICATION,
                RequestTaskActionType.AVIATION_VIR_SUBMIT_APPLICATION
            );
        }
    },

    AVIATION_VIR_APPLICATION_REVIEW(true, RequestType.AVIATION_VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_VIR_SAVE_REVIEW,
                RequestTaskActionType.AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    AVIATION_VIR_WAIT_FOR_REVIEW(true, RequestType.AVIATION_VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AVIATION_VIR_RFI_RESPONSE_SUBMIT(false, RequestType.AVIATION_VIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    AVIATION_VIR_WAIT_FOR_RFI_RESPONSE(false, RequestType.AVIATION_VIR, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS(true, RequestType.AVIATION_VIR, RequestExpirationType.AVIATION_VIR) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS,
                RequestTaskActionType.AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS
            );
        }
    },

    /*
     ************************************************* AVIATION UKETS *************************************************
     */

    /**
     * EMP_ISSUANCE_UKETS
     */
    EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT(true, RequestType.EMP_ISSUANCE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_SUBMIT_APPLICATION,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT
            );
        }
    },

    EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW(true, RequestType.EMP_ISSUANCE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    EMP_ISSUANCE_UKETS_APPLICATION_REVIEW(true, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_REVIEW,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW,
                RequestTaskActionType.EMP_ISSUANCE_UKETS_REVIEW_RETURN_FOR_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    EMP_ISSUANCE_UKETS_MAKE_PAYMENT(false, RequestType.EMP_ISSUANCE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_UKETS_TRACK_PAYMENT(false, RequestType.EMP_ISSUANCE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT(false, RequestType.EMP_ISSUANCE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_UKETS_RFI_RESPONSE_SUBMIT(false, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE(false, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    EMP_ISSUANCE_UKETS_RDE_RESPONSE_SUBMIT(false, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    EMP_ISSUANCE_UKETS_WAIT_FOR_RDE_RESPONSE(false, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_FORCE_DECISION,
                RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS(true, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_UKETS_RECALL_FROM_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT);
        }
    },

    EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT(true, RequestType.EMP_ISSUANCE_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.EMP_ISSUANCE_UKETS_SAVE_APPLICATION_AMEND,
                    RequestTaskActionType.EMP_ISSUANCE_UKETS_SUBMIT_APPLICATION_AMEND,
                    RequestTaskActionType.EMP_ISSUANCE_UKETS_UPLOAD_SECTION_ATTACHMENT);
        }
    },

    EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW(true, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.EMP_ISSUANCE_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_ISSUANCE_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_SUBMIT);
        }
    },
    
    /**
     * EMP_VARIATION_UKETS_REGULATOR_LED
     */
    EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT(true, RequestType.EMP_VARIATION_UKETS){
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
    		return List.of(
    				RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REGULATOR_LED,
    				RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
    				RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED,
    				RequestTaskActionType.EMP_VARIATION_CANCEL_APPLICATION,
    				RequestTaskActionType.EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED,
    				RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED
    				);
        }
    },
    
    EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },
    EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED
            );
        }
    },

    EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(

            );
        }
    },

    /**
     * EMP_VARIATION_UKETS_OPERATOR_LED
     */
    EMP_VARIATION_UKETS_APPLICATION_SUBMIT(true, RequestType.EMP_VARIATION_UKETS) {
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
			return List.of(
					RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION,
                    RequestTaskActionType.EMP_VARIATION_UKETS_SUBMIT_APPLICATION,
                    RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.EMP_VARIATION_CANCEL_APPLICATION);
        }
    },
    
    EMP_VARIATION_UKETS_WAIT_FOR_REVIEW(true, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    EMP_VARIATION_UKETS_APPLICATION_REVIEW(true, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_REVIEW,
            		RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
            		RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION,
            		RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION,
            		RequestTaskActionType.EMP_VARIATION_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
            		RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_REVIEW_DETERMINATION,
            		RequestTaskActionType.EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION,
            		RequestTaskActionType.EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW,
            		RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_RETURN_FOR_AMENDS,
            		RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
            		);
        }
    },
    
    EMP_VARIATION_UKETS_RFI_RESPONSE_SUBMIT(false, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    EMP_VARIATION_UKETS_WAIT_FOR_RFI_RESPONSE(false, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    EMP_VARIATION_UKETS_RDE_RESPONSE_SUBMIT(false, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    EMP_VARIATION_UKETS_WAIT_FOR_RDE_RESPONSE(false, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_FORCE_DECISION,
                RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },
    
    EMP_VARIATION_UKETS_MAKE_PAYMENT(false, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_VARIATION_UKETS_TRACK_PAYMENT(false, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_VARIATION_UKETS_CONFIRM_PAYMENT(false, RequestType.EMP_VARIATION_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },
    
    EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW(true, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
        	return List.of(
        			RequestTaskActionType.EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION
        			);
        }
    },

    EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    EMP_VARIATION_UKETS_WAIT_FOR_AMENDS(true, RequestType.EMP_VARIATION_UKETS, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.EMP_VARIATION_UKETS_RECALL_FROM_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT);
        }
    },

    EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT(true, RequestType.EMP_VARIATION_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.EMP_VARIATION_UKETS_SAVE_APPLICATION_AMEND,
                    RequestTaskActionType.EMP_VARIATION_UKETS_SUBMIT_APPLICATION_AMEND,
                    RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT);
        }
    },

    /**
     * AVIATION_AER_UKETS
     */
    AVIATION_AER_UKETS_APPLICATION_SUBMIT(true, RequestType.AVIATION_AER_UKETS, RequestExpirationType.AVIATION_AER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION,
                RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION,
                RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_VERIFICATION
            );
        }
    },

    AVIATION_AER_UKETS_APPLICATION_REVIEW(true, RequestType.AVIATION_AER_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.AVIATION_AER_UKETS_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.AVIATION_AER_UKETS_COMPLETE_REVIEW,
                RequestTaskActionType.AVIATION_AER_UKETS_SKIP_REVIEW,
                RequestTaskActionType.AVIATION_AER_UKETS_REVIEW_RETURN_FOR_AMENDS
            );
        }
    },

    AVIATION_AER_UKETS_WAIT_FOR_REVIEW(true, RequestType.AVIATION_AER_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AVIATION_AER_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION,
                RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION
            );
        }
    },

    AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION(true, RequestType.AVIATION_AER_UKETS, RequestExpirationType.AVIATION_AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION);
        }
    },

    AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT(true, RequestType.AVIATION_AER_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION,
                RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND
                );
        }
    },

    AVIATION_AER_UKETS_WAIT_FOR_AMENDS(true, RequestType.AVIATION_AER_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AVIATION_AER_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION,
                RequestTaskActionType.AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION
            );
        }
    },

    AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION(true, RequestType.AVIATION_AER_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION);
        }
    },

    /**
     * AVIATION_DRE_UKETS
     */
    AVIATION_DRE_UKETS_APPLICATION_SUBMIT(true, RequestType.AVIATION_DRE_UKETS){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_DRE_UKETS_SAVE_APPLICATION,
                RequestTaskActionType.AVIATION_DRE_UPLOAD_ATTACHMENT,
                RequestTaskActionType.AVIATION_DRE_CANCEL_APPLICATION,
                RequestTaskActionType.AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW,
                RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR
            );
        }
    },

    AVIATION_DRE_UKETS_MAKE_PAYMENT(true, RequestType.AVIATION_DRE_UKETS, RequestExpirationType.PAYMENT) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    AVIATION_DRE_UKETS_TRACK_PAYMENT(true, RequestType.AVIATION_DRE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    AVIATION_DRE_UKETS_CONFIRM_PAYMENT(true, RequestType.AVIATION_DRE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW(true, RequestType.AVIATION_DRE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW(true, RequestType.AVIATION_DRE_UKETS) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    /*
     ************************************************* AVIATION CORSIA *************************************************
     */

    /**
     * EMP_ISSUANCE_CORSIA
     */
    EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT(true, RequestType.EMP_ISSUANCE_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_SECTION_ATTACHMENT
            );
        }
    },

    EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW(true, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW(true, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_REVIEW_RETURN_FOR_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    EMP_ISSUANCE_CORSIA_RFI_RESPONSE_SUBMIT(false, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    EMP_ISSUANCE_CORSIA_WAIT_FOR_RFI_RESPONSE(false, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    EMP_ISSUANCE_CORSIA_RDE_RESPONSE_SUBMIT(false, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    EMP_ISSUANCE_CORSIA_WAIT_FOR_RDE_RESPONSE(false, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_FORCE_DECISION,
                RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    EMP_ISSUANCE_CORSIA_MAKE_PAYMENT(false, RequestType.EMP_ISSUANCE_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_CORSIA_TRACK_PAYMENT(false, RequestType.EMP_ISSUANCE_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT(false, RequestType.EMP_ISSUANCE_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW(true, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_SUBMIT);
        }
    },

    EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS(true, RequestType.EMP_ISSUANCE_CORSIA, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_RECALL_FROM_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT);
        }
    },

    EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT(true, RequestType.EMP_ISSUANCE_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND,
                RequestTaskActionType.EMP_ISSUANCE_CORSIA_UPLOAD_SECTION_ATTACHMENT);
        }
    },
    
    /**
     * EMP_VARIATION_CORSIA_OPERATOR_LED
     */
    EMP_VARIATION_CORSIA_APPLICATION_SUBMIT(true, RequestType.EMP_VARIATION_CORSIA) {
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
			return List.of(
					RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION,
                    RequestTaskActionType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION,
                    RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.EMP_VARIATION_CANCEL_APPLICATION);
        }
    },
    
    EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    EMP_VARIATION_CORSIA_APPLICATION_REVIEW(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW,
            		RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION,
            		RequestTaskActionType.EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW,
                    RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_RETURN_FOR_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
                    );
        }
    },
    
    EMP_VARIATION_CORSIA_RFI_RESPONSE_SUBMIT(false, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    EMP_VARIATION_CORSIA_WAIT_FOR_RFI_RESPONSE(false, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.RFI) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    EMP_VARIATION_CORSIA_RDE_RESPONSE_SUBMIT(false, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    EMP_VARIATION_CORSIA_WAIT_FOR_RDE_RESPONSE(false, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.RDE) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RDE_FORCE_DECISION,
                RequestTaskActionType.RDE_UPLOAD_ATTACHMENT
                );
        }
    },
    
    EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_CORSIA_RECALL_FROM_AMENDS,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT);
        }
    },

    EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT(true, RequestType.EMP_VARIATION_CORSIA, RequestExpirationType.APPLICATION_REVIEW){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND,
                RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT);
        }
    },

    EMP_VARIATION_CORSIA_MAKE_PAYMENT(false, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_VARIATION_CORSIA_TRACK_PAYMENT(false, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_VARIATION_CORSIA_CONFIRM_PAYMENT(false, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * EMP_VARIATION_CORSIA_REGULATOR_LED
     */
    EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED,
                RequestTaskActionType.EMP_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED,
                RequestTaskActionType.EMP_VARIATION_CANCEL_APPLICATION,
                RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED,
                RequestTaskActionType.EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_REGULATOR_LED
            );
        }
    },
    
    EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED
            );
        }
    },

    EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(

            );
        }
    },

    EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    EMP_VARIATION_CORSIA_REGULATOR_LED_TRACK_PAYMENT(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT(true, RequestType.EMP_VARIATION_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    /**
     * AVIATION_AER_CORSIA
     */
    AVIATION_AER_CORSIA_APPLICATION_SUBMIT(true, RequestType.AVIATION_AER_CORSIA, RequestExpirationType.AVIATION_AER) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION,
                    RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.AVIATION_AER_CORSIA_SUBMIT_APPLICATION,
                    RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION
            );
        }
    },

    AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AVIATION_AER_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION,
                    RequestTaskActionType.AVIATION_AER_CORSIA_SUBMIT_APPLICATION_VERIFICATION
            );
        }
    },

    AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION(true, RequestType.AVIATION_AER_CORSIA, RequestExpirationType.AVIATION_AER){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION);
        }
    },

    AVIATION_AER_CORSIA_APPLICATION_REVIEW(true, RequestType.AVIATION_AER_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.AVIATION_AER_CORSIA_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                RequestTaskActionType.AVIATION_AER_CORSIA_SKIP_REVIEW,
                RequestTaskActionType.AVIATION_AER_CORSIA_COMPLETE_REVIEW,
                RequestTaskActionType.AVIATION_AER_CORSIA_REVIEW_RETURN_FOR_AMENDS
            );
        }
    },

    AVIATION_AER_CORSIA_WAIT_FOR_REVIEW(true, RequestType.AVIATION_AER_CORSIA) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT(true, RequestType.AVIATION_AER_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND,
                    RequestTaskActionType.AVIATION_AER_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION
            );
        }
    },

    AVIATION_AER_CORSIA_WAIT_FOR_AMENDS(true, RequestType.AVIATION_AER_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT(true, RequestType.AVIATION_AER_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION,
                RequestTaskActionType.AVIATION_AER_CORSIA_SUBMIT_APPLICATION_VERIFICATION
            );
        }
    },

    AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION(true, RequestType.AVIATION_AER_CORSIA){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.AVIATION_AER_RECALL_FROM_VERIFICATION);
        }
    },


    /*
     ******************************************* SYSTEM_MESSAGE_NOTIFICATION *******************************************
     */

    /**
     * SYSTEM_MESSAGE_NOTIFICATION
     */
    ACCOUNT_USERS_SETUP(false, RequestType.SYSTEM_MESSAGE_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    },

    NEW_VERIFICATION_BODY_EMITTER(false, RequestType.SYSTEM_MESSAGE_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    },
    VERIFIER_NO_LONGER_AVAILABLE(false, RequestType.SYSTEM_MESSAGE_NOTIFICATION) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    };


    private final boolean assignable;
	private final RequestType requestType;
    private final RequestExpirationType expirationKey;

    private RequestTaskType(boolean assignable, RequestType requestType) {
    	this(assignable, requestType, null);
    }

    private RequestTaskType(boolean assignable, RequestType requestType, RequestExpirationType expirationKey) {
    	this.assignable = assignable;
    	this.requestType = requestType;
    	this.expirationKey = expirationKey;
    }

    public abstract List<RequestTaskActionType> getAllowedRequestTaskActionTypes();

    public static Optional<RequestTaskType> fromString(String value) {
    	return Arrays.stream(values())
    	          .filter(type -> type.name().equals(value))
    	          .findFirst();
    }

    public boolean isExpirable() {
    	return expirationKey != null;
    }

    public static Set<RequestTaskType> getSystemMessageNotificationTypes() {
        return Set.of(
            ACCOUNT_USERS_SETUP,
            VERIFIER_NO_LONGER_AVAILABLE,
                NEW_VERIFICATION_BODY_EMITTER
        );
    }

    public static Set<RequestTaskType> getPeerReviewTypes() {
        return Set.of(
        		PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
        		PERMIT_SURRENDER_APPLICATION_PEER_REVIEW,
        		PERMIT_REVOCATION_APPLICATION_PEER_REVIEW,
        		PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW,
        		PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW,
        		PERMIT_VARIATION_APPLICATION_PEER_REVIEW,
                PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW,
                NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW,
                DRE_APPLICATION_PEER_REVIEW,
                NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW,
                NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW,
                NER_APPLICATION_PEER_REVIEW,
                DOAL_APPLICATION_PEER_REVIEW,
                WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
                RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
                EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
                AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW,
                EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW,
                EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW,
                AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW,
                AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW,
                EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW,
                EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW,
                EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW
        );
    }

    public static Set<RequestTaskType> getMakePaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(MAKE_PAYMENT.name()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getTrackPaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(TRACK_PAYMENT.name()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getConfirmPaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(CONFIRM_PAYMENT.name()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RFI_RESPONSE_SUBMIT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RFI_RESPONSE.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRdeResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RDE_RESPONSE_SUBMIT.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRdeWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RDE_RESPONSE.name()))
                .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRfiRdeWaitForResponseTypes() {
		return Stream.concat(getRfiWaitForResponseTypes().stream(), getRdeWaitForResponseTypes().stream())
				.collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getWaitForRequestTaskTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.toString().contains("WAIT_FOR"))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getTaskTypesRelatedToVerifier() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.toString().endsWith(APPLICATION_VERIFICATION_SUBMIT.name()))
                .collect(Collectors.toSet());
    }
}
