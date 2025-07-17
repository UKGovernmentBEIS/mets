package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import org.springframework.jdbc.core.RowMapper;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.flightaircraftprocedures.EmpOperatingStatePairsCorsiaDetails;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmpOperatorDetailsCorsiaRowMapper implements RowMapper<EtsEmpOperatorDetailsCorsia> {

	@Override
    public EtsEmpOperatorDetailsCorsia mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpOperatorDetailsCorsia.builder()
                .fldEmitterID(rs.getString("fldEmitterID"))
                .empVersion(rs.getInt("empVersion"))
                .serviceContactName(rs.getString("srvCntName"))
                .serviceContactEmail(rs.getString("srvCntEmail"))
                .aeroplaneOperatorName(rs.getString("Aeroplane_operator_name"))
                .flightOptions(rs.getString("Flight_options"))
                .uniqueIcaoDesignator(rs.getString("Unique_icao_designator"))
                .aeroplaneRegistrationMarkings(rs.getString("Aeroplane_registration_markings") != null ? Stream.of(rs.getString("Aeroplane_registration_markings").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet()) : null)
                .operatingCertificateExist(rs.getBoolean("Air_operating_certificate_yes"))
                .aocNumber(rs.getBoolean("Air_operating_certificate_yes") ? rs.getString("Aoc_number") : null)
                .aocIssuingAuthority(rs.getBoolean("Air_operating_certificate_yes") ? rs.getString("Issuing_authority_name") : null)
                .aocStoredFileName(rs.getString("aoc_storedFileName"))
                .aocUploadedFileName(rs.getString("aoc_uploadedFileName"))
                .restrictionExist(rs.getBoolean("Restriction_yes"))
                .restrictionDetails(rs.getBoolean("Restriction_yes") ? rs.getString("Restriction_details") : null)
                .legalStatus(rs.getString("Aircraft_operator_legal_status"))
                .registeredCompanyExist(rs.getBoolean("Registered_company_details_yes"))
                .companyRegistrationNumber(!rs.getBoolean("Registered_company_details_yes") ? rs.getString("Company_registration_number") : null)
                .registeredCompanyRegistrationNumber(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_company_registration_number") : null)
                .registeredAddressLine1(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_address_line_1") : null)
                .registeredAddressLine2(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_address_line_2") : null)
                .registeredCity(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_city") : null)
                .registeredStateProvinceRegion(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_state_province_region") : null)
                .registeredPostcode(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_postcode_zip") : null)
                .registeredCountry(rs.getBoolean("Registered_company_details_yes") ? rs.getString("Registered_country") : null)
                .companyLtdAddressLine1(rs.getString("Company_ltd_address_line1"))
                .companyLtdAddressLine2(rs.getString("Company_ltd_address_line2"))
                .companyLtdCity(rs.getString("Company_ltd_city"))
                .companyLtdStateProvinceRegion(rs.getString("Company_ltd_state_province_region"))
                .companyLtdPostcode(rs.getString("Company_ltd_postcode_zip"))
                .companyLtdCountry(rs.getString("Company_ltd_country"))
                .aircraftOperatorContactFirstName(rs.getString("Aircraft_operator_contact_firstname"))
                .aircraftOperatorContactSurname(rs.getString("Aircraft_operator_contact_surname"))
                .aoAddressLine1(rs.getString("Ao_address_line_1"))
                .aoAddressLine2(rs.getString("Ao_address_line_2"))
                .aoCity(rs.getString("Ao_city"))
                .aoStateProvinceRegion(rs.getString("Ao_state_province_region"))
                .aoPostcode(rs.getString("Ao_postcode_zip"))
                .aoCountry(rs.getString("Ao_country"))
                .partnershipName(rs.getString("Partnership_name"))
                .partnerAddressLine1(rs.getString("Partner_address_line_1"))
                .partnerAddressLine2(rs.getString("Partner_address_line_2"))
                .partnerCity(rs.getString("Partner_city"))
                .partnerStateProvinceRegion(rs.getString("Partner_state_province_region"))
                .partnerPostcode(rs.getString("Partner_postcode_zip"))
                .partnerCountry(rs.getString("Partner_country"))
                .partners(rs.getString("Partners") != null ? Stream.of(rs.getString("Partners").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet()) : null)
                .operatorType(rs.getString("Commercial_non_commercial"))
                .carriedOutFlights(rs.getString("Carried_out_flights"))
                .activitiesSummary(rs.getString("Activities_summary"))
                .emissionSourcesProcedureTitle(rs.getString("Corsia_additional_procedures_Emission_sources_procedure_title"))
                .emissionSourcesProcedureReference(rs.getString("Corsia_additional_procedures_Emission_sources_procedure_reference"))
                .emissionSourcesProcedureDescription(rs.getString("Corsia_additional_procedures_Emission_sources_procedure_description"))
                .emissionSourcesDataMaintenancePost(rs.getString("Corsia_additional_procedures_Emission_sources_data_maintenance_post"))
                .emissionSourcesRecordsLocation(rs.getString("Corsia_additional_procedures_Emission_sources_records_location"))
                .emissionSourcesSystemName(rs.getString("Corsia_additional_procedures_Emission_sources_system_name"))
                .flightsListProcedureTitle(rs.getString("Corsia_additional_procedures_Flights_list_procedure_title"))
                .flightsListProcedureReference(rs.getString("Corsia_additional_procedures_Flights_list_procedure_reference"))
                .flightsListProcedureDescription(rs.getString("Corsia_additional_procedures_Flights_list_procedure_description"))
                .flightsListDataMaintenancePost(rs.getString("Corsia_additional_procedures_Flights_list_data_maintenance_post"))
                .flightsListRecordsLocation(rs.getString("Corsia_additional_procedures_Flights_list_records_location"))
                .flightsListSystemName(rs.getString("Corsia_additional_procedures_Flights_list_system_name"))
                .statePairs(rs.getString("state_pairs") != null ? resolveStatePairs(rs.getString("state_pairs")) : null)
                .internationalProcedureDetailsProcedureTitle(rs.getString("International_procedure_details_Procedure_title"))
                .internationalProcedureDetailsProcedureReference(rs.getString("International_procedure_details_Procedure_reference"))
                .internationalProcedureDetailsProcedureDescription(rs.getString("International_procedure_details_Procedure_description"))
                .internationalProcedureDetailsDataMaintenancePost(rs.getString("International_procedure_details_Data_maintenance_post"))
                .internationalProcedureDetailsRecordsLocation(rs.getString("International_procedure_details_Records_location"))
                .internationalProcedureDetailsSystemName(rs.getString("International_procedure_details_System_name")) 
                .procedureTitle(rs.getString("Procedure_title"))
                .procedureReference(rs.getString("Procedure_reference"))
                .procedureDescription(rs.getString("Procedure_description"))
                .dataMaintenancePost(rs.getString("Data_maintenance_post"))
                .recordsLocation(rs.getString("Records_location"))
                .systemName(rs.getString("System_name"))         
                .procedureDetailsNoMonitoringProcedureTitle(rs.getString("Procedure_details_no_monitoring_Procedure_title"))
                .procedureDetailsNoMonitoringProcedureReference(rs.getString("Procedure_details_no_monitoring_Procedure_reference"))
                .procedureDetailsNoMonitoringProcedureDescription(rs.getString("Procedure_details_no_monitoring_Procedure_description"))
                .procedureDetailsNoMonitoringDataMaintenancePost(rs.getString("Procedure_details_no_monitoring_Data_maintenance_post"))
                .procedureDetailsNoMonitoringRecordsLocation(rs.getString("Procedure_details_no_monitoring_Records_location"))
                .procedureDetailsNoMonitoringSystemName(rs.getString("Procedure_details_no_monitoring_System_name"))
                .build();
    }

	private Set<EmpOperatingStatePairsCorsiaDetails> resolveStatePairs(String statePairsString) {
		List<String> pairs = Stream.of(statePairsString
				.split("\\s*\\|\\s*"))
				.filter(s -> !s.isEmpty())
				.map(String::trim)
				.toList();
		
		return pairs.stream()
        .map(s -> 
        	EmpOperatingStatePairsCorsiaDetails
        	.builder()
        	.stateA(s.split("\\s*,\\s*")[0])
        	.stateB(s.split("\\s*,\\s*")[1])
        	.build()
        	)
        .collect(Collectors.toSet());
	}
}
