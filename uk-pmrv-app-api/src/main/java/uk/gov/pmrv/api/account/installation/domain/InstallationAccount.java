package uk.gov.pmrv.api.account.installation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "account_installation")
@SqlResultSetMapping(
    name = InstallationAccount.ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER,
    classes = {
        @ConstructorResult(
            targetClass = AccountSearchResultsInfoDTO.class,
            columns = {
                @ColumnResult(name = "id", type = Long.class),
                @ColumnResult(name = "name"),
                @ColumnResult(name = "emitterId"),
                @ColumnResult(name = "status", type = String.class),
                @ColumnResult(name = "legalEntityName")
            }
        )})

@NamedQuery(
        name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_FULL_INFO_BY_ID,
        query = "select ac "
                + "from InstallationAccount ac "
                + "join fetch ac.location "
                + "join fetch ac.legalEntity le "
                + "join fetch le.location le_loc "
                + "left join fetch le.holdingCompany hc "
                + "where ac.id = (:id)")

@NamedQuery(
        name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LE_BY_ID,
        query = "select ac "
                + "from InstallationAccount ac "
                + "join fetch ac.legalEntity le "
                + "where ac.id = (:id)")

@NamedQuery(
        name = InstallationAccount.NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LOC_AND_LE_BY_ID,
        query = "select ac "
                + "from InstallationAccount ac "
                + "join fetch ac.location "
                + "join fetch ac.legalEntity le "
                + "join fetch le.location le_loc "
                + "where ac.id = (:id)")
public class InstallationAccount extends Account {

    public static final String ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER = "AccountSearchResultsInfoDTOResultMapper";
    public static final String NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LE_BY_ID = "Account.findInstallationAccountWithLeById";
    public static final String NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_FULL_INFO_BY_ID = "Account.findInstallationAccountFullInfoById";
    public static final String NAMED_QUERY_FIND_INSTALLATION_ACCOUNT_WITH_LOC_AND_LE_BY_ID = "Account.findInstallationAccountWithLocAndLeById";

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private InstallationAccountStatus status;

    @Column(name = "site_name")
    @NotBlank
    private String siteName;

    @Enumerated(EnumType.STRING)
    @Column(name = "emitter_type")
    private EmitterType emitterType;

    @Enumerated(EnumType.STRING)
    @Column(name = "installation_category")
    private InstallationCategory installationCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "application_type")
    @NotNull
    private ApplicationType applicationType;

    @Column(name = "transfer_code", unique = true)
    private String transferCode;

    @Column(name = "transfer_code_status")
    @Enumerated(EnumType.STRING)
    private TransferCodeStatus transferCodeStatus;

    @Column(name = "fa_status")
    private boolean faStatus;
}
