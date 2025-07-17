package uk.gov.pmrv.api.migration.emp.corsia.operatordetails.subsidiarycompanies;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.operatordetails.SubsidiaryCompanyCorsia;
import uk.gov.netz.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.attachments.FileAttachmentUtil;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;
import uk.gov.pmrv.api.migration.emp.corsia.operatordetails.EmpOperatorDetailsCorsiaMigrationMapper;
import uk.gov.pmrv.api.migration.emp.corsia.operatordetails.EmpOperatorDetailsFlightIdentificationCorsia;
import uk.gov.netz.api.referencedata.domain.Country;
import uk.gov.netz.api.referencedata.repository.CountryRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpSubsidiaryCompaniesCorsiaMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpOperatorDetailsFlightIdentificationCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;
	private final CountryRepository countryRepository;

	public EmpSubsidiaryCompaniesCorsiaMigrationService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
														CountryRepository countryRepository) {
		this.migrationJdbcTemplate = migrationJdbcTemplate;
		this.countryRepository = countryRepository;
	}

	private static final String BLANK_FILE_PATH = "migration" + File.separator + "attachments" + File.separator + "Blank file AOC not required.pdf";
    private static final String QUERY_BASE  = "select * from (values\r\n"
    		+ "('65E39678-4C8F-4548-8097-9E4F0007876B',14900,'BRITISH AIRWAYS PLC','BA Cityflyer Ltd','Option 1','CFE','','true','GB2314','United Kingdom - Civil Aviation Authority','false','','02571224','Pioneer House, The Tower Business Park','Wilmslow Road','Manchester','','M20 2BA','United Kingdom','Scheduled and non-scheduled flights','CityFlyer is a subsidiary airline of British Airways plc operating short-haul flights in Europe, based at London City airport. CityFlyer uses the designator CFE. The airline also leases aeroplanes when required.'),\r\n"
    		+ "('65E39678-4C8F-4548-8097-9E4F0007876B',14900,'BRITISH AIRWAYS PLC','BA EuroFlyer Ltd','Option 1','EFW','','true','GB2488','United Kingdom - Civil Aviation Authority','false','','13734241','PO Box 365, Waterside','Speedbird Way','Harmondsworth','','UB7 0GB','United Kingdom','Scheduled and non-scheduled flights','EuroFlyer is a subsidiary airline of British Airways plc operating short-haul flights in Europe, based at London Gatwick airport. EuroFlyer uses the designator EFW. The airline also leases aeroplanes when required.')\r\n"
    		+ ") t (fldEmitterID,fldEmitterDisplayID,Operator_Name,Subsidiary_Aeroplane_operator_name,Aircraft_identification,Unique_icao_designator,Aeroplane_registration_markings,Aoc_yes,Certificate_number,Issuing_authority,Aoc_restriction_yes,Restriction_summary,Company_reg_number,Address_line_1,Address_line_2,City,State_province_region,Postcode_zip,Country,Activity_type,Activity_summary)\r\n";
    
    @Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {        
        String query = constructSectionQuery(QUERY_BASE, List.of());
        Map<String, List<SubsidiaryCompanyCorsia>> sections = executeQuery(query)
        		.stream()
                .collect(Collectors.groupingBy(EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia::getFldEmitterID))
                .entrySet()
              .stream()
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> EmpOperatorDetailsCorsiaMigrationMapper.toSubsidiaryCompanies(entry.getValue())));       
        // replace country name with country code
        sections.values().forEach(set -> set.forEach(company -> replaceCountryNameWithCode(company.getRegisteredLocation())));
        
        sections
            .forEach((etsAccId, section) -> {
            	emps.get(accountsToMigrate.get(etsAccId).getId())
                	.getEmpContainer().getEmissionsMonitoringPlan().getOperatorDetails().setSubsidiaryCompanyExist(true);
            	emps.get(accountsToMigrate.get(etsAccId).getId())
                	.getEmpContainer().getEmissionsMonitoringPlan().getOperatorDetails().setSubsidiaryCompanies(section);
            	
            	// subsidiary companies don't have attachments in etswap, set blank file
            	EmissionsMonitoringPlanMigrationCorsiaContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                EmissionsMonitoringPlanCorsiaContainer empContainer = empMigrationContainer.getEmpContainer();
            	section.forEach(company -> {
            		FileAttachment blankFileAttachment = FileAttachmentUtil.getFileAttachment(BLANK_FILE_PATH);
            		UUID blankFileAttachmentUuid = UUID.fromString(blankFileAttachment.getUuid());
            		company.getAirOperatingCertificate().setCertificateFiles(Set.of(blankFileAttachmentUuid));
            		empContainer.getEmpAttachments().put(blankFileAttachmentUuid, blankFileAttachment.getFileName());
                	empMigrationContainer.getFileAttachments().add(blankFileAttachment);
            	});
            });	
	}
    
    @Override
    public Map<String, EmpOperatorDetailsFlightIdentificationCorsia> querySection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }


	private List<EtsEmpOperatorDetailsSubsidiaryCompaniesCorsia> executeQuery(String query) {
    	return migrationJdbcTemplate.query(query,
                new EtsEmpOperatorDetailsSubsidiaryCompaniesCorsiaRowMapper());		
    }
	
	private void replaceCountryNameWithCode(LocationOnShoreStateDTO locationOnShoreStateDTO) {
        String country = countryRepository.findByName(locationOnShoreStateDTO.getCountry())
                .map(Country::getCode)
                .orElse(locationOnShoreStateDTO.getCountry());
        locationOnShoreStateDTO.setCountry(country);
    }
}
