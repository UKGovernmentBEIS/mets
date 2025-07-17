package uk.gov.pmrv.api.migration.caexternalcontact;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.service.CaExternalContactService;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MigrationCaExternalContactService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final CaExternalContactService caExternalContactService;
    private final Validator validator;

    public MigrationCaExternalContactService(@Nullable @Qualifier("migrationJdbcTemplate") JdbcTemplate migrationJdbcTemplate,
                                             CaExternalContactService caExternalContactService,
                                             Validator validator) {
        this.migrationJdbcTemplate = migrationJdbcTemplate;
        this.caExternalContactService = caExternalContactService;
        this.validator = validator;
    }

    private static final String QUERY_BASE =
        "select "
            + " ec.fldExternalContactID as id,\r\n"
            + " ec.fldName as name,\r\n"
            + " c.fldEmailAddress as email,\r\n"
            + " ec.fldDescription as description,\r\n"
            + " ca.fldName as competentAuthority\r\n"
            + "from tblExternalContact ec\r\n"
            + "inner join tblContact c on c.fldContactID = ec.fldContactID\r\n"
            + "inner join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = ec.fldCompetentAuthorityID\r\n";

    @Override
    public String getResource() {
        return "ca-external-contacts";
    }

    public List<String> migrate(String ids) {
        final String query = constructQuery(ids);

        List<Map<String, Object>> results = migrationJdbcTemplate.queryForList(query);

        List<String> failedEntries = new ArrayList<>();
        for (Map<String, Object> entry : results) {
            String id = (String) entry.get("id");
            String name = (String) entry.get("name");
            String email = (String) entry.get("email");
            String description = (String) entry.get("description");
            String etsCompAuth = (String) entry.get("competentAuthority");
            CompetentAuthorityEnum ca = MigrationHelper.resolveCompAuth(etsCompAuth);
            if (ca == null) {
                failedEntries.add(
                    constructErrorMessage(id, name, email, null, "PMRV CA cannot be resolved for ETS CA", etsCompAuth));
                continue;
            }

            AppUser authUser = new AppUser();
            authUser.setRoleType(RoleTypeConstants.REGULATOR);
            authUser.setAuthorities(List.of(AppAuthority.builder().competentAuthority(ca).build()));

            CaExternalContactRegistrationDTO externalContactDTO =
                CaExternalContactRegistrationDTO.builder()
                    .name(name)
                    .email(email)
                    .description(description).build();

            //validate dto
            Set<ConstraintViolation<CaExternalContactRegistrationDTO>> constraintViolations =
                validator.validate(externalContactDTO);
            if (!constraintViolations.isEmpty()) {
                constraintViolations.forEach(
                    v -> failedEntries.add(constructErrorMessage(id, name, email, ca, v.getMessage(),
                        v.getPropertyPath().iterator().next().getName() + ": " + v.getInvalidValue())));
                continue;
            }

            //create
            try {
                caExternalContactService.createCaExternalContact(authUser, externalContactDTO);
            } catch (Exception e) {
                failedEntries.add(constructErrorMessage(id, name, email, ca, e.getMessage(), null));
            }
        }

        return failedEntries;
    }

    private String constructQuery(String ids) {
        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);

        List<String> idList =
            !StringUtils.isBlank(ids) ? new ArrayList<>(Arrays.asList(ids.split(","))) : new ArrayList<>();
        if (!idList.isEmpty()) {
            queryBuilder.append(" where ec.fldExternalContactID in (");
            idList.forEach(id -> queryBuilder.append("'").append(id).append("'").append(","));
            queryBuilder.deleteCharAt(queryBuilder.length() - 1);
            queryBuilder.append(")");
        }
        queryBuilder.append(";");
        return queryBuilder.toString();
    }

    private String constructErrorMessage(String id, String name, String email, CompetentAuthorityEnum ca,
                                         String errorMessage, String data) {
        return "Id: " + id +
            " | Name: " + name +
            " | Email: " + email +
            " | CA: " + ca +
            " | Error: " + errorMessage +
            " | data: " + data;
    }

}
