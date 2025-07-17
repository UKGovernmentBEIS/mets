package uk.gov.pmrv.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uk.gov.netz.api.common.exception.NetzErrorCode;

/**
 * Error Status enumerator with error codes.
 */
@Getter
public enum MetsErrorCode implements NetzErrorCode {

    /** Codes for Account errors. */
    ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS("ACCOUNT1001", HttpStatus.BAD_REQUEST, "Account registration number already exists"),
    ACCOUNT_FIELD_NOT_AMENDABLE("ACCOUNT1003", HttpStatus.BAD_REQUEST, "Non amendable account fields"),
    LEGAL_ENTITY_NOT_ASSOCIATED_WITH_USER("ACCOUNT1000", HttpStatus.BAD_REQUEST, "Legal entity is not associated with the user", true),
    LEGAL_ENTITY_ALREADY_EXISTS("ACCOUNT1002", HttpStatus.BAD_REQUEST, "Legal entity name already exists"),

    CRCO_CODE_ALREADY_RELATED_WITH_ANOTHER_ACCOUNT("ACCOUNT1012", HttpStatus.BAD_REQUEST, "Central route charges office number already related with another account"),
    REGISTRY_ID_SUBMITTED_ONLY_FOR_UK_ETS_AVIATION_ACCOUNTS("ACCOUNT1013", HttpStatus.BAD_REQUEST, "Registry Id is only submitted when emission trading scheme of the account is UK ETS Aviation"),

    AVIATION_ACCOUNT_REPORTING_STATUS_NOT_CHANGED("ACCOUNT1014", HttpStatus.BAD_REQUEST, "Aviation account reporting status is not changed"),
    AVIATION_ACCOUNT_LOCATION_NOT_EXIST("ACCOUNT1015", HttpStatus.NOT_FOUND, "Aviation Account location doesn't exist"),
    INVALID_ACCOUNT_TYPE("ACCOUNT1016", HttpStatus.BAD_REQUEST, "Invalid account type"),

    /** Codes for notification errors. */
    INVALID_DOCUMENT_TEMPLATE_FOR_REQUEST_TASK("NOTIF1005", HttpStatus.BAD_REQUEST,"Document template does not match request task type"),
    DOCUMENT_TEMPLATE_COMMON_PARAMS_PROVIDER_NOT_FOUND("NOTIF1003", HttpStatus.INTERNAL_SERVER_ERROR, "Document template common params provider not founf"),

    /** Codes for Permit errors. */
    INVALID_PERMIT_REVIEW("PERMIT1002", HttpStatus.BAD_REQUEST, "Invalid Permit review"),
    INVALID_PERMIT("PERMIT1003", HttpStatus.BAD_REQUEST, "Invalid Permit"),
    INVALID_PERMIT_VARIATION_REVIEW("PERMIT1004", HttpStatus.BAD_REQUEST, "Invalid Permit Variation Review"),
    INVALID_PERMIT_SURRENDER("PERMITSURRENDER1001", HttpStatus.BAD_REQUEST, "Invalid Permit surrender"),
    INVALID_PERMIT_TRANSFER("PERMITTRANSFER1001", HttpStatus.BAD_REQUEST, "Invalid Permit transfer"),
    INVALID_PERMIT_NOTIFICATION("PERMITNOTIFICATION1001", HttpStatus.BAD_REQUEST, "Invalid Permit notification"),
    INVALID_PERMIT_NOTIFICATION_NOT_SUPPORTED_TEMPORARY_SUSPENSION("PERMITNOTIFICATION1002", HttpStatus.BAD_REQUEST, "Not supported Permit notification of type temporary suspension"),
    INVALID_PERMIT_NOTIFICATION_CESSATION_DECISION("PERMITNOTIFICATION1003", HttpStatus.BAD_REQUEST, "Permit notification type Cessation has mismatch decision type"),
    INVALID_PERMIT_NOTIFICATION_DECISION("PERMITNOTIFICATION1004", HttpStatus.BAD_REQUEST, "Permit notification type has mismatch decision type"),

    BATCH_REISSUE_IN_PROGRESS_REQUEST_EXISTS("BATCHREISSUE0001", HttpStatus.BAD_REQUEST, "In progress batch reissue exists"),
    BATCH_REISSUE_ZERO_EMITTERS_SELECTED("BATCHREISSUE0002", HttpStatus.BAD_REQUEST, "0 emitters selected"),
    REISSUE_ACCOUNT_NOT_APPLICABLE("REISSUE1001", HttpStatus.BAD_REQUEST, "Invalid reissue account"),

    /** Codes for NER. */
    INVALID_NER("NER1001", HttpStatus.BAD_REQUEST, "Invalid NER"),

    /** Codes for DOAL. */
    INVALID_DOAL("DOAL1001", HttpStatus.BAD_REQUEST, "Invalid DOAL"),

    /** Codes for BDR. */
    BDR_CREATION_NOT_ALLOWED("BDR1000", HttpStatus.BAD_REQUEST, "BDR creation is not allowed"),
    INVALID_BDR_REVIEW("BDR1001", HttpStatus.BAD_REQUEST, "Invalid BDR review"),
    BDR_MUST_UNDERGO_VERIFICATION("BDR1002", HttpStatus.BAD_REQUEST, "BDR must undergo verification"),
    INVALID_BDR_REVIEW_OUTCOME("BDR1003", HttpStatus.BAD_REQUEST, "Invalid BDR review outcome"),
    BDR_PRIMARY_CONTACT_NOT_FOUND("BDR1004", HttpStatus.NOT_FOUND, "Account primary contact not found"),
    BDR_FILENAME_NOT_VALID("BDR1005", HttpStatus.BAD_REQUEST, "BDR file name is not valid"),
    BDR_REQUEST_IS_NOT_BDR("BDR1006", HttpStatus.BAD_REQUEST, "Provided request id is not of type BDR"),

    /** Codes for ALR. */
    ALR_CREATION_NOT_ALLOWED("ALR1000", HttpStatus.BAD_REQUEST, "ALR creation is not allowed"),
    ALR_FILENAME_NOT_VALID("ALR1001", HttpStatus.BAD_REQUEST, "ALR file name is not valid"),


    /** Codes for AER. */
    AER_CREATION_NOT_ALLOWED("AER1000", HttpStatus.BAD_REQUEST, "AER creation is not allowed"),
    INVALID_AER("AER1001", HttpStatus.BAD_REQUEST, "Invalid AER"),
    AER_EMISSIONS_CALCULATION_FAILED("AER1002", HttpStatus.BAD_REQUEST,"AER emissions calculation failed"),
    AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION("AER1003", HttpStatus.BAD_REQUEST, "Invalid measurement units combination detected during AER emissions calculation"),
    AER_EMISSIONS_CALCULATION_PARAMETER_VALUE_MISSING("AER1004", HttpStatus.BAD_REQUEST, "Mandatory calculation parameter is missing"),
    INVALID_AER_VERIFICATION_REPORT("AER1005", HttpStatus.BAD_REQUEST, "Invalid AER verification report"),
    AER_REQUEST_IS_NOT_AER("AER1006", HttpStatus.BAD_REQUEST, "Provided request id is not of type AER"),
    INVALID_AER_REVIEW("AER1007", HttpStatus.BAD_REQUEST, "Invalid Aer review"),
    INVALID_SOURCE_STREAM_TYPE("AER1008", HttpStatus.BAD_REQUEST, "Invalid Source Stream type"),

    /** Codes for VIR. */
    VIR_CREATION_NOT_ALLOWED("VIR1000", HttpStatus.BAD_REQUEST, "VIR creation is not allowed"),
    INVALID_VIR("VIR1001", HttpStatus.BAD_REQUEST, "Invalid VIR"),
    INVALID_VIR_REVIEW("VIR1002", HttpStatus.BAD_REQUEST, "Invalid VIR review"),

    /** Codes for AIR. */
    INVALID_AIR("AIR1001", HttpStatus.BAD_REQUEST, "Invalid AIR"),
    INVALID_AIR_REVIEW("AIR1002", HttpStatus.BAD_REQUEST, "Invalid AIR review"),
    
    /**Emissions Monitoring Plan error codes */
    INVALID_EMP("EMP1001", HttpStatus.BAD_REQUEST, "Invalid Emissions Monitoring Plan"),
    INVALID_EMP_REVIEW("EMP1002", HttpStatus.BAD_REQUEST, "Invalid Emissions Monitoring Plan review"),
    NO_EMP_SERVICE_FOUND("EMP1003", HttpStatus.NOT_FOUND, "No associated Emissions Monitoring Plan service found"),
    INVALID_EMP_VARIATION_REVIEW("EMP1004", HttpStatus.BAD_REQUEST, "Invalid Emissions Monitoring Plan variation review"),

    /** Codes for AVIATION_AER. */
    AVIATION_AER_CREATION_NOT_ALLOWED_INVALID_ACCOUNT_STATUS("AVIATIONAER1000", HttpStatus.BAD_REQUEST, "AER creation is not allowed. Invalid account status"),
    AVIATION_AER_ALREADY_EXISTS_FOR_REPORTING_YEAR("AVIATIONAER1001", HttpStatus.BAD_REQUEST, "AER creation is not allowed. AER already exists for the provided reporting year"),
    INVALID_AVIATION_AER("AVIATIONAER1002", HttpStatus.BAD_REQUEST, "Invalid AER"),
    INVALID_AVIATION_AER_VERIFICATION_REPORT("AVIATIONAER1003", HttpStatus.BAD_REQUEST, "Invalid AER verification report"),
    NO_AVIATION_AER_SERVICE_FOUND("AVIATIONAER1004", HttpStatus.NOT_FOUND, "No associated Aviation AER service found"),
    INVALID_AVIATION_AER_REVIEW("AVIATIONAER1005", HttpStatus.BAD_REQUEST, "Invalid AER review"),

    /** Codes for Allowances. */
    INVALID_ALLOWANCE_ALLOCATIONS("ALLOWANCE1001", HttpStatus.BAD_REQUEST, "Invalid Allowance Allocation values"),

    /** Codes for registry integration. */

    INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_AER_NOT_FOUND("INTREGEMISSIONSINST1000", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no aer request has been found"),
    INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_WRONG_AER_INITIATOR_TYPE("INTREGEMISSIONSINST1001", HttpStatus.INTERNAL_SERVER_ERROR,  "Emissions updated are not sent to ETS Registry because AER initiator was not within the allowed types"),
    INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_FROM_NOT_FOUND("INTREGEMISSIONSINST1002", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.from property has been found"),
    INTEGRATION_REGISTRY_EMISSIONS_INSTALLATION_REPORTING_PERIOD_TO_NOT_FOUND("INTREGEMISSIONSINST1003", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no configuration for the aer.installation.reporting-period.to property has been found"),

    INTEGRATION_REGISTRY_EMISSIONS_AVIATION_REPORTING_PERIOD_FROM_NOT_FOUND("INTREGEMISSIONSAVUKETS1004", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no configuration for the aer.aviation.reporting-period.from property has been found"),
    INTEGRATION_REGISTRY_EMISSIONS_AVIATION_REPORTING_PERIOD_TO_NOT_FOUND("INTREGEMISSIONSAVUKETS1005", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no configuration for the aer.aviation.reporting-period.to property has been found"),
    INTEGRATION_REGISTRY_EMISSIONS_AVIATION_AER_NOT_FOUND("INTREGEMISSIONSAVUKETS1006", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because no aer request has been found"),
    INTEGRATION_REGISTRY_EMISSIONS_KAFKA_QUEUE_CONNECTION_ISSUE("INTREGEMISSIONSINSTAVUKETS1007", HttpStatus.INTERNAL_SERVER_ERROR, "Cannot send emissions to ETS Registry because kafka message queue is not available"),
    ;

    /** The error code. */
    private final String code;

    /** The http status. */
    private final HttpStatus httpStatus;

    /** The message. */
    private final String message;

    /** Whether the error is security related */
    private boolean security;

    MetsErrorCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    MetsErrorCode(String code, HttpStatus httpStatus, String message, boolean isSecurity) {
        this(code, httpStatus, message);
        this.security = isSecurity;
    }
}
