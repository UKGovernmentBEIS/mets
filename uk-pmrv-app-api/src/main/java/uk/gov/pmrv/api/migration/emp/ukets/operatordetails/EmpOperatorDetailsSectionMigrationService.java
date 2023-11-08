package uk.gov.pmrv.api.migration.emp.ukets.operatordetails;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreStateDTO;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.LimitedCompanyOrganisation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationLegalStatusType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails.OrganisationStructure;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.files.attachments.domain.FileAttachment;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.emp.common.attachments.FileAttachmentUtil;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanMigrationContainer;
import uk.gov.pmrv.api.migration.emp.ukets.EmissionsMonitoringPlanSectionMigrationService;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;
import uk.gov.pmrv.api.referencedata.domain.Country;
import uk.gov.pmrv.api.referencedata.repository.CountryRepository;

import static uk.gov.pmrv.api.migration.emp.common.MigrationEmissionsMonitoringPlanHelper.constructSectionQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class EmpOperatorDetailsSectionMigrationService implements EmissionsMonitoringPlanSectionMigrationService<EmpOperatorDetailsFlightIdentification> {

    private final JdbcTemplate migrationJdbcTemplate;

    private final CountryRepository countryRepository;

    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    private static final String BLANK_FILE_PATH = "migration" + File.separator + "attachments" + File.separator + "Blank file AOC not required.pdf";
    private static final String QUERY_BASE = "with XMLNAMESPACES (\n"
    		+ "    'urn:www-toplev-com:officeformsofd' AS fd\n"
    		+ "), r1 as (\n"
    		+ "    select F.fldEmitterID, e.fldEmitterDisplayID, F.fldFormStatusTypeID, FD.fldFormID, FD.fldMajorVersion, fd.fldMinorVersion, FD.fldDateUpdated, FD.fldFormDataID, FD.fldSubmittedXML,\n"
    		+ "           max(fd.fldMajorVersion) over (partition by f.fldFormID) maxMajorVersion\n"
    		+ "      from tblEmitter e\n"
    		+ "      join tblForm F             on f.fldEmitterID = e.fldEmitterID\n"
    		+ "      join tblFormData FD        on FD.fldFormID = f.fldFormID and FD.fldMinorVersion = 0\n"
    		+ "      join tlnkFormTypePhase FTP on F.fldFormTypePhaseID = FTP.fldFormTypePhaseID\n"
    		+ "      join tlkpFormType FT       on FTP.fldFormTypeID = FT.fldFormTypeID and FT.fldName = 'AEMPlan'\n"
    		+ "      join tlkpPhase P           on FTP.fldPhaseID = P.fldPhaseID and P.fldDisplayName = 'Phase 3'\n"
    		+ "), r2 as (\n"
    		+ "    select r1.*,\n"
    		+ "           nullif(fldSubmittedXML.value('(fd:formdata/fielddata/Scope_eu_ets_yes)[1]', 'nvarchar(max)'), '') scopeUkEts\n"
    		+ "      from r1\n"
    		+ "     where fldMajorVersion = maxMajorVersion \n"
    		+ "), ar as (\n"
    		+ "    select fldEmitterID, string_agg(nullif(trim(T.c.query('Aircraft_registration_markings').value('.', 'nvarchar(max)')), ''), ',') Aircraft_registration_markings\n"
    		+ "      from r2\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Operator_details-Aircraft_registration/row)') as T(c)\n"
    		+ "     group by fldEmitterID\n"
    		+ "), aoc as (\n"
    		+ "    select p.fldEmitterID,\n"
    		+ "           T2.c.value('@name[1]      ', 'nvarchar(max)') uploadedFileName,\n"
    		+ "           T2.c.value('@fileName[1]  ', 'nvarchar(max)') storedFileName\n"
    		+ "      from r2 p\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/attachfields/attachfield[@fieldname=\"Operation_details:Aoc_evidence_copy\"])') as T1(c)\n"
    		+ "     cross apply p.fldSubmittedXML.nodes('(fd:formdata/linkedAttachments/linkedAttachment)') as T2(c)\n"
    		+ "     where T2.c.value('@name[1]', 'nvarchar(max)') = T1.c.value('@attachname[1]', 'nvarchar(max)')\n"
    		+ "    union all\n"
    		+ "    select *\n"
    		+ "      from (values\n"
    		+ "            ('4B9C7E1C-D1EA-4AE6-8D35-ADB300BCBECC', 'Air Operator Cert.PDF', '76551.att'),\n"
    		+ "            ('9BFAFA88-1821-4BA9-89B2-9E4F000D77F9', 'AOC 8.pdf', '41175.att'),\n"
    		+ "            ('5EB8FD75-02C8-45BD-AA97-9E4F0008C7DC', 'DAW AOC.pdf', '32760.att'),\n"
    		+ "            ('15B4D478-1FFE-43A4-8A16-9ECB00E9844C', 'Jetstream Aviation Operating Certificate.pdf', '60354.att')\n"
    		+ "         ) parameter_correction (c1, c2, c3)\n"
    		+ "), pd as (\n"
    		+ "    select fldEmitterID, string_agg(concat_ws(' ', nullif(trim(T.c.query('First_name').value('.', 'nvarchar(max)')), ''), nullif(trim(T.c.query('Surname').value('.', 'nvarchar(max)')), '')), ',') Partners\n"
    		+ "      from r2\n"
    		+ "     cross apply fldSubmittedXML.nodes('(fd:formdata/fielddata/Operator_details-Partner_details/row)') as T(c)\n"
    		+ "     group by fldEmitterID\n"
    		+ ")\n"
    		+ "select e.fldEmitterID, e.fldEmitterDisplayID, fldMajorVersion empVersion, \n"
    		+ "       -- Issue date\n"
    		+ "       coalesce(nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Od_assessment_date)[1]', 'nvarchar(max)')), ''),\n"
    		+ "                nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Odu_assessment_completion_date)[1]', 'nvarchar(max)')), '')\n"
    		+ "       ) assessment_completion_date,\n"
    		+ "       -- Service contact\n"
    		+ "       concat_ws(' ', nullif(trim(c.fldName), ''), nullif(trim(c.fldSurname), '')) srvCntName,\n"
    		+ "       nullif(trim(c.fldEmailAddress), '') srvCntEmail,\n"
    		+ "       -- Identification\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_name                  )[1]', 'nvarchar(max)')), '') Aircraft_operator_name     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_unique_id             )[1]', 'nvarchar(max)')), '') Aircraft_operator_unique_id,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Icao_designator_yes                     )[1]', 'nvarchar(max)')), '') Icao_designator_yes        ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Unique_icao_designator                  )[1]', 'nvarchar(max)')), '') Unique_icao_designator     ,\n"
    		+ "       ar.Aircraft_registration_markings,\n"
    		+ "       -- Air Operating Certificate Details\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Air_operating_certificate_yes           )[1]', 'nvarchar(max)')), '') Air_operating_certificate_yes,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aoc_number                              )[1]', 'nvarchar(max)')), '') Aoc_number                   ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Issuing_authority_name                  )[1]', 'nvarchar(max)')), '') Issuing_authority_name       ,\n"
    		+ "       -- Aoc evidence\n"
    		+ "       aoc.storedFileName aoc_storedFileName, aoc.uploadedFileName aoc_uploadedFileName,\n"
    		+ "       -- Operating Licence Details\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Operating_license_yes                   )[1]', 'nvarchar(max)')), '') Operating_license_yes                   ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Operator_license_ref_number             )[1]', 'nvarchar(max)')), '') Operator_license_ref_number             ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Operation_licence_issuing_authority_name)[1]', 'nvarchar(max)')), '') Operation_licence_issuing_authority_name,\n"
    		+ "       -- Organisation structure\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_legal_status          )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_legal_status,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_company_details_yes          )[1]', 'NVARCHAR(max)')), '') Registered_company_details_yes,\n"
    		+ "       -- Company / Limited Liability Partnership - Not Registered_company\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_registration_number             )[1]', 'NVARCHAR(max)')), '') Company_registration_number,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_address_line1               )[1]', 'NVARCHAR(max)')), '') Company_ltd_address_line1        ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_address_line2               )[1]', 'NVARCHAR(max)')), '') Company_ltd_address_line2        ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_city                        )[1]', 'NVARCHAR(max)')), '') Company_ltd_city                 ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_state_province_region       )[1]', 'NVARCHAR(max)')), '') Company_ltd_state_province_region,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_postcode_zip                )[1]', 'NVARCHAR(max)')), '') Company_ltd_postcode_zip         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Company_ltd_country                     )[1]', 'NVARCHAR(max)')), '') Company_ltd_country              ,\n"
    		+ "       -- Company / Limited Liability Partnership - Registered_company\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_company_registration_number  )[1]', 'NVARCHAR(max)')), '') Registered_company_registration_number,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_address_line_1               )[1]', 'NVARCHAR(max)')), '') Registered_address_line_1       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_address_line_2               )[1]', 'NVARCHAR(max)')), '') Registered_address_line_2       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_city                         )[1]', 'NVARCHAR(max)')), '') Registered_city                 ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_state_province_region        )[1]', 'NVARCHAR(max)')), '') Registered_state_province_region,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_postcode_zip                 )[1]', 'NVARCHAR(max)')), '') Registered_postcode_zip         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Registered_country                      )[1]', 'NVARCHAR(max)')), '') Registered_country              ,\n"
    		+ "       -- Individual / Sole Trader\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_contact_firstname     )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_contact_firstname,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Aircraft_operator_contact_surname       )[1]', 'NVARCHAR(max)')), '') Aircraft_operator_contact_surname  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_address_line_1                       )[1]', 'NVARCHAR(max)')), '') Ao_address_line_1       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_address_line_2                       )[1]', 'NVARCHAR(max)')), '') Ao_address_line_2       ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_city                                 )[1]', 'NVARCHAR(max)')), '') Ao_city                 ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_state_province_region                )[1]', 'NVARCHAR(max)')), '') Ao_state_province_region,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_postcode_zip                         )[1]', 'NVARCHAR(max)')), '') Ao_postcode_zip         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Ao_country                              )[1]', 'NVARCHAR(max)')), '') Ao_country              ,\n"
    		+ "       -- Partnership\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partnership_name                        )[1]', 'NVARCHAR(max)')), '') Partnership_name,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_address_line_1                  )[1]', 'NVARCHAR(max)')), '') Partner_address_line_1           ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_address_line_2                  )[1]', 'NVARCHAR(max)')), '') Partner_address_line_2           ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_city                            )[1]', 'NVARCHAR(max)')), '') Partner_city                     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_state_province_region           )[1]', 'NVARCHAR(max)')), '') Partner_state_province_region    ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_postcode_zip                    )[1]', 'NVARCHAR(max)')), '') Partner_postcode_zip             ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operator_details-Partner_country                         )[1]', 'NVARCHAR(max)')), '') Partner_country                  ,\n"
    		+ "       pd.Partners,\n"
    		+ "       -- Operation activities\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operation_details-Commercial_non_commercial)[1]', 'NVARCHAR(max)')), '') Commercial_non_commercial,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operation_details-Carried_out_flights      )[1]', 'NVARCHAR(max)')), '') Carried_out_flights      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operation_details-Aviation_activity        )[1]', 'NVARCHAR(max)')), '') Aviation_activity        ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operation_details-Activity_desc            )[1]', 'NVARCHAR(max)')), '') Activity_desc            ,\n"
    		+ "       -- Flights and Aircraft monitoring procedures\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_procedure_title      )[1]', 'NVARCHAR(max)')), '') Emission_sources_procedure_title      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_procedure_reference  )[1]', 'NVARCHAR(max)')), '') Emission_sources_procedure_reference  ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_procedure_description)[1]', 'NVARCHAR(max)')), '') Emission_sources_procedure_description,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_data_maintenance_post)[1]', 'NVARCHAR(max)')), '') Emission_sources_data_maintenance_post,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_records_location     )[1]', 'NVARCHAR(max)')), '') Emission_sources_records_location     ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Emission_sources_system_name          )[1]', 'NVARCHAR(max)')), '') Emission_sources_system_name          ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_procedure_title          )[1]', 'NVARCHAR(max)')), '') Flights_list_procedure_title          ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_procedure_reference      )[1]', 'NVARCHAR(max)')), '') Flights_list_procedure_reference      ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_procedure_description    )[1]', 'NVARCHAR(max)')), '') Flights_list_procedure_description    ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_data_maintenance_post    )[1]', 'NVARCHAR(max)')), '') Flights_list_data_maintenance_post    ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_records_location         )[1]', 'NVARCHAR(max)')), '') Flights_list_records_location         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Flights_list_system_name              )[1]', 'NVARCHAR(max)')), '') Flights_list_system_name              ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_procedure_title               )[1]', 'NVARCHAR(max)')), '') Annex_1_procedure_title               ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_procedure_reference           )[1]', 'NVARCHAR(max)')), '') Annex_1_procedure_reference           ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_procedure_description         )[1]', 'NVARCHAR(max)')), '') Annex_1_procedure_description         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_data_maintenance_post         )[1]', 'NVARCHAR(max)')), '') Annex_1_data_maintenance_post         ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_records_location              )[1]', 'NVARCHAR(max)')), '') Annex_1_records_location              ,\n"
    		+ "       nullif(trim(fldSubmittedXML.value('(fd:formdata/fielddata/Operating_procedures-Annex_1_system_name                   )[1]', 'NVARCHAR(max)')), '') Annex_1_system_name\n"
    		+ "  from tblEmitter e\n"
    		+ "  join tblAviationEmitter ae on ae.fldEmitterID  = e.fldEmitterID\n"
    		+ "                            and ae.fldCommissionListName = 'UK ETS'\n"
    		+ "  join tlkpEmitterStatus  es on e.fldEmitterStatusID = es.fldEmitterStatusID\n"
    		+ "                            and es.fldDisplayName in ('Commission List','Exempt - Commercial','Exempt - Non-Commercial','Live')\n"
    		+ "  join      r2               on r2.fldEmitterID  = e.fldEmitterID and scopeUkEts = 'true'\n"
    		+ "  left join ar               on ar.fldEmitterID  = e.fldEmitterID\n"
    		+ "  left join aoc              on aoc.fldEmitterID = e.fldEmitterID\n"
    		+ "  left join pd               on pd.fldEmitterID  = e.fldEmitterID\n"
    		+ "  left join tblContact c     on c.fldContactID   = e.fldServiceContactID\n";

    @Override
    public void populateSection(Map<String, Account> accountsToMigrate, Map<Long, EmissionsMonitoringPlanMigrationContainer> emps) {
        Map<String, EmpOperatorDetailsFlightIdentification> sections =
                queryEtsSection(new ArrayList<>(accountsToMigrate.keySet()));


        sections
                .forEach((etsAccId, section) -> {
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setOperatorDetails(section.getOperatorDetails());
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().getEmissionsMonitoringPlan().setFlightAndAircraftProcedures(section.getFlightAndAircraftProcedures());
                    emps.get(accountsToMigrate.get(etsAccId).getId())
                            .getEmpContainer().setServiceContactDetails(section.getServiceContactDetails());

                    EmissionsMonitoringPlanMigrationContainer empMigrationContainer = emps.get(accountsToMigrate.get(etsAccId).getId());
                    EmissionsMonitoringPlanUkEtsContainer empContainer = empMigrationContainer.getEmpContainer();
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
    public Map<String, EmpOperatorDetailsFlightIdentification> queryEtsSection(List<String> accountIds) {
        //construct query
        String query = constructSectionQuery(QUERY_BASE, accountIds);

        List<EtsEmpOperatorDetails> etsEmpOperatorDetails = executeQuery(query, accountIds);

        Map<String, EmpOperatorDetailsFlightIdentification> operatorDetailsFlightIdentificationMap =
                EmpOperatorDetailsMigrationMapper.toOperatorDetailsIdentifications(etsEmpOperatorDetails);

        operatorDetailsFlightIdentificationMap.
                forEach((key, value) -> replaceCountryNameWithCode(value.getOperatorDetails().getOrganisationStructure()));
        return operatorDetailsFlightIdentificationMap;
    }

    private List<EtsEmpOperatorDetails> executeQuery(String query, List<String> accountIds) {
        return migrationJdbcTemplate.query(query,
                new EtsEmpOperatorDetailsRowMapper(),
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
