package uk.gov.pmrv.api.migration.emp.corsia.operatordetails.subsidiarycompanies;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EtsEmpOperatorDetailsSubsidiaryCompaniesCorsiaRowMapper implements RowMapper<EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia> {

	@Override
    public EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia mapRow(ResultSet rs, int rowNum) throws SQLException {

        return EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia.builder()
                .fldEmitterID(rs.getString("fldEmitterID"))
                .operatorName(rs.getString("Operator_Name"))
                .subsidiaryAeroplaneOperatorName(rs.getString("Subsidiary_Aeroplane_operator_name"))
                .aircraftIdentification(rs.getString("Aircraft_identification"))
                .uniqueIcaoDesignator(rs.getString("Unique_icao_designator"))
                .aeroplaneRegistrationMarkings(rs.getString("Aeroplane_registration_markings") != null ? Stream.of(rs.getString("Aeroplane_registration_markings").split("\\s*,\\s*"))
                        .filter(s -> !s.isEmpty())
                        .map(String::trim)
                        .collect(Collectors.toSet()) : null)
                .operatingCertificateExist(rs.getBoolean("Aoc_yes"))
                .aocNumber(rs.getBoolean("Aoc_yes") ? rs.getString("Certificate_number") : null)
                .aocIssuingAuthority(rs.getBoolean("Aoc_yes") ? rs.getString("Issuing_authority") : null)
                .restrictionExist(rs.getBoolean("Aoc_restriction_yes"))
                .restrictionDetails(rs.getBoolean("Aoc_restriction_yes") ? rs.getString("Restriction_summary") : null)
                .companyRegistrationNumber(rs.getString("Company_reg_number"))
                .registeredAddressLine1(rs.getString("Address_line_1"))
                .registeredAddressLine2(rs.getString("Address_line_2"))
                .registeredCity(rs.getString("City"))
                .registeredStateProvinceRegion(rs.getString("State_province_region"))
                .registeredPostcode(rs.getString("Postcode_zip"))
                .registeredCountry(rs.getString("Country"))
                .activityType(rs.getString("Activity_type"))
                .activitySummary(rs.getString("Activity_summary"))
                .build();
    }
}
