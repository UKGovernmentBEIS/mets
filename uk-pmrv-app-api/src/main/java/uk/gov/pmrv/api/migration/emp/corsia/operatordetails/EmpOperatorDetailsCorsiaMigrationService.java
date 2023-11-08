package uk.gov.pmrv.api.migration.emp.corsia.operatordetails;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.attachments.FileAttachmentUtil;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanCorsiaSectionMigrationService;
import uk.gov.pmrv.api.migration.emp.corsia.EmissionsMonitoringPlanMigrationCorsiaContainer;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpOperatorDetailsCorsiaMigrationService 
	implements EmissionsMonitoringPlanCorsiaSectionMigrationService<EmpOperatorDetailsFlightIdentificationCorsia> {

	private final JdbcTemplate migrationJdbcTemplate;

    private final CountryRepository countryRepository;

    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    private static final String BLANK_FILE_PATH = "migration" + File.separator + "attachments" + File.separator + "Blank file AOC not required.pdf";
    private static final String QUERY_BASE = "with XMLNAMESPACES (\r\n"
    		+ "    'urn:www-toplev-com:officeformsofd' AS fd\r\n"
    		+ "), r1 as (\r\n"
    		+ "    select F.fldEmitterID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\r\n"
    		+ "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\r\n"
    		+ "      from tblEmitter e\r\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\r\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\r\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\r\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\r\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\r\n"
    		+ "), r2 as (\r\n"
    		+ "    select r1.*,\r\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_corsia_yes)[1]', 'nvarchar(max)'), '') scopeCorsia\r\n"
    		+ "      from r1\r\n"
    		+ "     where fldMajorVersion = maxMajorVersion \r\n"
    		+ "), arm as (\r\n"
    		+ "    select fldEmitterID, string_agg(nullif(trim(T.c.value('Aeroplane_registration_markings[1]', 'nvarchar(max)')), ''), ',') Aeroplane_registration_markings\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Corsia_operator_details-Aeroplane_registration/row)') as T(c)\r\n"
    		+ "     group by fldEmitterID\r\n"
    		+ "), aoc as (\r\n"
    		+ "    select p.fldEmitterID,\r\n"
    		+ "           T2.c.value('@name[1]    ', 'nvarchar(max)') uploadedFileName,\r\n"
    		+ "           T2.c.value('@fileName[1]', 'nvarchar(max)') storedFileName\r\n"
    		+ "      from r2 p\r\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname=\"Corsia_operator_details:Aoc_evidence_copy\"])') as T1(c)\r\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)\r\n"
    		+ "     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')\r\n"
    		+ "    --union all\r\n"
    		+ "    --select *\r\n"
    		+ "    --  from (values\r\n"
    		+ "    --        ('00000000-0000-0000-0000-000000000000', 'filename.pdf', '00000.att')\r\n"
    		+ "    --     ) parameter_correction (c1, c2, c3)\r\n"
    		+ "), pd as (\r\n"
    		+ "    select fldEmitterID, string_agg(concat_ws(' ', nullif(trim(T.c.value('First_name[1]', 'nvarchar(max)')), ''), nullif(trim(T.c.value('Surname[1]', 'nvarchar(max)')), '')), ',') Partners\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Corsia_operator_details-Partner_details/row)') as T(c)\r\n"
    		+ "     group by fldEmitterID\r\n"
    		+ "), sp as (\r\n"
    		+ "    select fldEmitterID,\r\n"
    		+ "           string_agg(concat(nullif(trim(T.c.value('State_a[1]', 'nvarchar(max)')), ''), ',', nullif(trim(T.c.value('State_b[1]', 'nvarchar(max)')), '')), '|') state_pairs\r\n"
    		+ "      from r2\r\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/State_pair/row)') as T(c)\r\n"
    		+ "     group by fldEmitterID\r\n"
    		+ ")\r\n"
    		+ "select e.fldEmitterID,\r\n"
    		+ "       m.fldEmitterDisplayID,\r\n"
    		+ "       fldMajorVersion empVersion, \r\n"
    		+ "       -- Service contact\r\n"
    		+ "       concat_ws(' ', nullif(trim(c.fldName), ''), nullif(trim(c.fldSurname), '')) srvCntName,\r\n"
    		+ "       nullif(trim(c.fldEmailAddress), '') srvCntEmail,\r\n"
    		+ "       -- Identification\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aeroplane_operator_name )[1]', 'nvarchar(max)')), '') Aeroplane_operator_name,\r\n"
    		+ "       case nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Flight_options     )[1]', 'nvarchar(max)')), '') when null then null when 'Option1' then 'ICAO_DESIGNATORS' else 'REGISTRATION_MARKINGS' end Flight_options,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Unique_icao_designator  )[1]', 'nvarchar(max)')), '') Unique_icao_designator,\r\n"
    		+ "       arm.Aeroplane_registration_markings,\r\n"
    		+ "       -- Air Operating Certificate Details\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Air_operating_certificate_yes     )[1]', 'nvarchar(max)')), '') Air_operating_certificate_yes,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aoc_number                        )[1]', 'nvarchar(max)')), '') Aoc_number,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Issuing_authority_name            )[1]', 'nvarchar(max)')), '') Issuing_authority_name,\r\n"
    		+ "       aoc.storedFileName aoc_storedFileName, aoc.uploadedFileName aoc_uploadedFileName, -- Aoc evidence\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Restriction_yes                   )[1]', 'nvarchar(max)')), '') Restriction_yes,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Restriction_details               )[1]', 'nvarchar(max)')), '') Restriction_details,\r\n"
    		+ "       -- Organisation structure\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aircraft_operator_legal_status    )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_legal_status,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_company_details_yes    )[1]', 'NVARCHAR(max)')), '') Registered_company_details_yes,\r\n"
    		+ "       -- Company / Limited Liability Partnership - Not Registered_company\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_registration_number       )[1]', 'NVARCHAR(max)')), '') Company_registration_number,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_address_line1         )[1]', 'NVARCHAR(max)')), '') Company_ltd_address_line1,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_address_line2         )[1]', 'NVARCHAR(max)')), '') Company_ltd_address_line2,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_city                  )[1]', 'NVARCHAR(max)')), '') Company_ltd_city,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_state_province_region )[1]', 'NVARCHAR(max)')), '') Company_ltd_state_province_region,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_postcode_zip          )[1]', 'NVARCHAR(max)')), '') Company_ltd_postcode_zip,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Company_ltd_country               )[1]', 'NVARCHAR(max)')), '') Company_ltd_country,\r\n"
    		+ "       -- Company / Limited Liability Partnership - Registered_company\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_company_registration_number )[1]', 'NVARCHAR(max)')), '') Registered_company_registration_number,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_address_line_1              )[1]', 'NVARCHAR(max)')), '') Registered_address_line_1,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_address_line_2              )[1]', 'NVARCHAR(max)')), '') Registered_address_line_2,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_city                        )[1]', 'NVARCHAR(max)')), '') Registered_city,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_state_province_region       )[1]', 'NVARCHAR(max)')), '') Registered_state_province_region,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_postcode_zip                )[1]', 'NVARCHAR(max)')), '') Registered_postcode_zip,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Registered_country                     )[1]', 'NVARCHAR(max)')), '') Registered_country,\r\n"
    		+ "       -- Individual / Sole Trader\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aircraft_operator_contact_firstname  )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_contact_firstname,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Aircraft_operator_contact_surname    )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_contact_surname ,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_address_line_1                    )[1]', 'NVARCHAR(max)')), '') Ao_address_line_1,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_address_line_2                    )[1]', 'NVARCHAR(max)')), '') Ao_address_line_2,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_city                              )[1]', 'NVARCHAR(max)')), '') Ao_city,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_state_province_region             )[1]', 'NVARCHAR(max)')), '') Ao_state_province_region,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_postcode_zip                      )[1]', 'NVARCHAR(max)')), '') Ao_postcode_zip,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Ao_country                           )[1]', 'NVARCHAR(max)')), '') Ao_country,\r\n"
    		+ "       -- Partnership\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partnership_name              )[1]', 'NVARCHAR(max)')), '') Partnership_name,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_address_line_1        )[1]', 'NVARCHAR(max)')), '') Partner_address_line_1,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_address_line_2        )[1]', 'NVARCHAR(max)')), '') Partner_address_line_2,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_city                  )[1]', 'NVARCHAR(max)')), '') Partner_city,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_state_province_region )[1]', 'NVARCHAR(max)')), '') Partner_state_province_region,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_postcode_zip          )[1]', 'NVARCHAR(max)')), '') Partner_postcode_zip,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Partner_country               )[1]', 'NVARCHAR(max)')), '') Partner_country,\r\n"
    		+ "       pd.Partners,\r\n"
    		+ "       -- Operation activities\r\n"
    		+ "       case when e.fldEmitterID = '1A7C9F00-482E-4A12-B9E5-A7270118790D' /*GLOBAL JET IOM LTD*/ then 'Non-commercial' else 'Commercial' end Commercial_non_commercial,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Carried_out_flights )[1]', 'NVARCHAR(max)')), '') Carried_out_flights,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_operator_details-Activities_summary  )[1]', 'NVARCHAR(max)')), '') Activities_summary,\r\n"
    		+ "\r\n"
    		+ "       -- Flights and Aircraft monitoring procedures\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_procedure_title       )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_procedure_reference   )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_procedure_description )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_data_maintenance_post )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_records_location      )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Emission_sources_system_name           )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Emission_sources_system_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_procedure_title       )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_procedure_reference   )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_procedure_description )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_data_maintenance_post )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_records_location      )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Corsia_additional_procedures-Flights_list_system_name           )[1]', 'NVARCHAR(max)')), '') Corsia_additional_procedures_Flights_list_system_name,\r\n"
    		+ "\r\n"
    		+ "       sp.state_pairs,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-Procedure_title       )[1]', 'NVARCHAR(max)')), '') International_procedure_details_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-Procedure_reference   )[1]', 'NVARCHAR(max)')), '') International_procedure_details_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-Procedure_description )[1]', 'NVARCHAR(max)')), '') International_procedure_details_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-Data_maintenance_post )[1]', 'NVARCHAR(max)')), '') International_procedure_details_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-Records_location      )[1]', 'NVARCHAR(max)')), '') International_procedure_details_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/International_procedure_details-System_name           )[1]', 'NVARCHAR(max)')), '') International_procedure_details_System_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_title       )[1]', 'NVARCHAR(max)')), '') Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_reference   )[1]', 'NVARCHAR(max)')), '') Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_description )[1]', 'NVARCHAR(max)')), '') Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Data_maintenance_post )[1]', 'NVARCHAR(max)')), '') Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Records_location      )[1]', 'NVARCHAR(max)')), '') Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/System_name           )[1]', 'NVARCHAR(max)')), '') System_name,\r\n"
    		+ "\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-Procedure_title       )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_Procedure_title,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-Procedure_reference   )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_Procedure_reference,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-Procedure_description )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_Procedure_description,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-Data_maintenance_post )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_Data_maintenance_post,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-Records_location      )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_Records_location,\r\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Procedure_details_no_monitoring-System_name           )[1]', 'NVARCHAR(max)')), '') Procedure_details_no_monitoring_System_name\r\n"
    		+ "  from tblEmitter e\r\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID  = e.fldEmitterID\r\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\r\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\r\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\r\n"
    		+ "  join      r2               on r2.fldEmitterID  = e.fldEmitterID and scopeCorsia = 'true'\r\n"
    		+ "  left join arm              on arm.fldEmitterID = e.fldEmitterID\r\n"
    		+ "  left join aoc              on aoc.fldEmitterID = e.fldEmitterID\r\n"
    		+ "  left join pd               on pd.fldEmitterID  = e.fldEmitterID\r\n"
    		+ "  left join sp               on sp.fldEmitterID  = e.fldEmitterID\r\n"
    		+ "  left join tblContact c     on c.fldContactID   = e.fldServiceContactID\r\n"
    		+ "  left join mig_aviation_emitters m on m.fldEmitterID = e.fldEmitterID and m.scope = 'CORSIA'\r\n";

    @Override
	public void populateSection(Map<String, Account> accountsToMigrate,
			Map<Long, EmissionsMonitoringPlanMigrationCorsiaContainer> emps) {
    	Map<String, EmpOperatorDetailsFlightIdentificationCorsia> sections =
                querySection(new ArrayList<>(accountsToMigrate.keySet()));

        sections
                .forEach((etsAccId, section) -> {
                	emps.get(accountsToMigrate.get(etsAccId).getId()).setConsolidationNumber(section.getEmpVersion());
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setOperatorDetails(section.getOperatorDetails());
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setFlightAndAircraftProcedures(section.getFlightAndAircraftProcedures());
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().setServiceContactDetails(section.getServiceContactDetails());

                    EmissionsMonitoringPlanMigrationCorsiaContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanCorsiaContainer empContainer = empMigrationContainer.getEmpContainer();
                    // handle attachments
                    List<EtsFileAttachment> attachments = section.getAttachments().getOrDefault(etsAccId, List.of());
                    if (section.getOperatorDetails().getAirOperatingCertificate().getCertificateExist()) {
                        Set<UUID> attachmentsUuids = attachments.stream().map(EtsFileAttachment::getUuid).collect(Collectors.toSet());
                        if (!attachmentsUuids.isEmpty()) {
                        	section.getOperatorDetails().getAirOperatingCertificate().setCertificateFiles(attachmentsUuids);
                        	attachments.forEach(file -> empContainer.getEmpAttachments().put(file.getUuid(), file.getUploadedFileName()));
                        	empMigrationContainer.getFileAttachments().addAll(etsFileAttachmentMapper.toFileAttachments(attachments));
                        } else {
                        	FileAttachment blankFileAttachment = FileAttachmentUtil.getFileAttachment(BLANK_FILE_PATH);
                        	UUID blankFileAttachmentUuid = UUID.fromString(blankFileAttachment.getUuid());
                        	section.getOperatorDetails().getAirOperatingCertificate().setCertificateFiles(Set.of(blankFileAttachmentUuid));
                        	empContainer.getEmpAttachments().put(blankFileAttachmentUuid, blankFileAttachment.getFileName());
                        	empMigrationContainer.getFileAttachments().add(blankFileAttachment);
                        }      
                    }          
                });	
	}

    @Override
    public Map<String, EmpOperatorDetailsFlightIdentificationCorsia> querySection(List<String> accountIds) {
        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpOperatorDetailsCorsia> empOperatorDetails = executeQuery(query, accountIds);

        Map<String, EmpOperatorDetailsFlightIdentificationCorsia> operatorDetailsFlightIdentificationMap =
        		EmpOperatorDetailsCorsiaMigrationMapper.toOperatorDetailsIdentifications(empOperatorDetails);

        operatorDetailsFlightIdentificationMap.
                forEach((key, value) -> replaceCountryNameWithCode(value.getOperatorDetails().getOrganisationStructure()));
        return operatorDetailsFlightIdentificationMap;
    }

    private List<EtsEmpOperatorDetailsCorsia> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpOperatorDetailsCorsiaRowMapper(),
                accountIds.isEmpty() ? new Object[]{} : accountIds.toArray());
    }

    private void replaceCountryNameWithCode(OrganisationStructure organisationStructure) {
        String country = countryRepository.findByName(organisationStructure.getOrganisationLocation().getCountry())
                .map(Country::getCode)
                .orElse(organisationStructure.getOrganisationLocation().getCountry());
        organisationStructure.getOrganisationLocation().setCountry(country);
        if (organisationStructure.getLegalStatusType().equals(OrganisationLegalStatusType.LIMITED_COMPANY)) {
            if (((LimitedCompanyOrganisation) organisationStructure).getDifferentContactLocationExist()) {
                LocationOnShoreStateDTO differentContactLocation =
                        ((LimitedCompanyOrganisation) organisationStructure).getDifferentContactLocation();
                String differentCountry = countryRepository.findByName(differentContactLocation.getCountry())
                        .map(Country::getCode)
                        .orElse(differentContactLocation.getCountry());
                differentContactLocation.setCountry(differentCountry);
            }
        }
    }
}
