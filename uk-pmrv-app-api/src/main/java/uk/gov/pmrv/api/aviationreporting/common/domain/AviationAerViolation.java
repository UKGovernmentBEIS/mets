package uk.gov.pmrv.api.aviationreporting.common.domain;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class AviationAerViolation {

    private String sectionName;
    private String message;
    private Object[] data;

    public AviationAerViolation(String sectionName, AviationAerViolation.AviationAerViolationMessage aerViolationMessage) {
        this(sectionName, aerViolationMessage, List.of());
    }

    public AviationAerViolation(String sectionName, AviationAerViolation.AviationAerViolationMessage aerViolationMessage,
                                Object... data) {
        this.sectionName = sectionName;
        this.message = aerViolationMessage.getMessage();
        this.data = data;
    }

    @Getter
    public enum AviationAerViolationMessage {

        INVALID_ISSUING_AUTHORITY("Invalid issuing authority"),
        INVALID_ICAO_CODE("Invalid airport icao code"),
        INVALID_DEPARTURE_AIRPORT_COUNTRY_TYPE("Invalid departure aerodrome country type"),
        INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_REPORTED_DEPARTURE("Invalid arrival aerodrome country type. When departure aerodrome is in UK ETS flights to EEA reported, departure aerodrome must be in UK or Gibraltar or European Economic Area states"),
        INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_NOT_REPORTED_DEPARTURE("Invalid arrival aerodrome country type. When departure aerodrome is in UK ETS flights to EEA not reported, departure aerodrome must be in UK only"),
        INVALID_DEPARTURE_ARRIVAL_AIRPORTS("Invalid departure and arrival aerodromes. Departure and arrival aerodromes must be different"),
        INVALID_DEPARTURE_ARRIVAL_STATES("Invalid departure and arrival states. Departure and arrival state must be different"),
        INVALID_COUNTRY_TYPE("Invalid airport country type"),
        INVALID_DATA_GAPS("Emissions monitoring approach type and data gaps are not mutually valid"),
        INVALID_AFFECTED_FLIGHTS_PERCENTAGE("Flights affected by data gaps percentage and calculated percentage are not equal"),
        INVALID_TOTAL_SAF_MASS("Total saf mass and calculated total saf mass are not equal"),
        INVALID_TOTAL_EMISSIONS_REDUCTION_CLAIM("Total emissions reduction claim and calculated total emissions reduction claim are not equal"),
        NO_VERIFICATION_REPORT_FOUND("No verification report found"),
        NO_VERIFICATION_REPORT_NEEDED("No verification report needed"),
        VERIFICATION_REPORT_EXISTS("Verification report exists"),
        VERIFICATION_REPORT_INVALID_UNCORRECTED_MISSTATEMENT_REFERENCE("Uncorrected misstatements reference format is not valid"),
        VERIFICATION_REPORT_INVALID_UNCORRECTED_NON_COMPLIANCES_REFERENCE("Uncorrected non compliances reference format is not valid"),
        VERIFICATION_REPORT_INVALID_UNCORRECTED_NON_CONFORMITIES_REFERENCE("Uncorrected non conformities reference format is not valid"),
        VERIFICATION_REPORT_INVALID_PRIOR_YEAR_ISSUE_REFERENCE("Prior year issue reference format is not valid"),
        VERIFICATION_REPORT_INVALID_RECOMMENDED_IMPROVEMENT_REFERENCE("Recommended Improvement reference format is not valid"),
        VERIFICATION_REPORT_INVALID_ERC_VERIFICATION_AND_SAF_EXISTS_COMBINATION("Emissions reduction claim verification and saf exist properties are not mutually valid"),
        VERIFICATION_REPORT_INVALID_ERC_VERIFICATION_AND_AER_EMISSIONS_REDUCTION_CLAIM_EXISTS_COMBINATION("Emissions reduction claim verification and aer emissions reduction claim exist properties are not mutually valid"),
        INVALID_AIRCRAFT_DATA_DESIGNATOR("Invalid ICAO designator"),
        INVALID_START_DATE_INTERVAL("Start Date cannot be a Date after the End Date"),
        INVALID_START_DATE_END_DATE_SCHEME_YEAR("Start Date and End Date must have the same scheme year"),
        INVALID_FLIGHT_TYPE_AND_FUEL_USE_COMBINATION("Flight type and fuel use monitoring properties are not mutually valid"),
        INVALID_TOTAL_EMISSIONS_DOCUMENTS("Total annual emissions data published and total annual emissions documents properties are not mutually valid"),
        INVALID_AGGREGATED_STATE_PAIR_DOCUMENTS("Aggregated state pair data published and aggregated state pair data documents properties are not mutually valid"),
        ;

        private final String message;

        AviationAerViolationMessage(String message) {
            this.message = message;
        }
    }
}
