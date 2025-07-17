package uk.gov.pmrv.api.account.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * The Account Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "account")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE,
        query = "select new uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO(acc.id, acc.name, VALUE(contacts)) "
                + "from Account acc "
                + "join acc.contacts contacts on KEY(contacts) = :contactType "
                + "where acc.id in (:accountIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID,
        query = "select acc "
                + "from Account acc "
                + "inner join acc.contacts contacts "
                + "where KEY(contacts) = :contactType "
                + "and contacts = :userId")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_IDS_BY_ACCOUNT_TYPE_AND_VB,
        query = "select acc.id "
                + "from Account acc "
                + "where acc.verificationBodyId = :vbId "
                + "and acc.accountType = :accountType ")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_WITH_CONTACTS_BY_VB_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "left join fetch ac.contacts "
                + "where ac.verificationBodyId in (:vbIds)")
@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_WITH_CONTACTS_IN_LIST,
        query = "select ac "
                + "from Account ac "
                + "left join fetch ac.contacts "
                + "where ac.verificationBodyId = :vbId "
                + "and ac.emissionTradingScheme in (:emissionTradingSchemes)")

@NamedQuery(
        name = Account.NAMED_QUERY_FIND_ACCOUNTS_WITH_LE_BY_ID_IN,
        query = "select ac "
                + "from Account ac "
                + "left join fetch ac.legalEntity le "
                + "where ac.id in (:ids)")
public abstract class Account {

    public static final String NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE = "Account.findAccountContactsByAccountIdsAndContactType";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID = "Account.findAccountsByContactTypeAndUserId";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_WITH_CONTACTS_BY_VB_IN_LIST = "Account.findAccountsWithContactsByVBInList";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_WITH_CONTACTS_IN_LIST = "Account.findAccountsByVBAndEmissionTradingSchemeWithContactsinList";
    public static final String NAMED_QUERY_FIND_IDS_BY_ACCOUNT_TYPE_AND_VB = "Account.findIdsByAccountTypeAndVB";
    public static final String NAMED_QUERY_FIND_ACCOUNTS_WITH_LE_BY_ID_IN = "Account.findAccountsWithLeByIdIn";

    @Id
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "legal_entity_id")
    private LegalEntity legalEntity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "location_id")
    private Location location;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull
    private AccountType accountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthorityEnum competentAuthority;

    @Column(name = "commencement_date")
    @NotNull
    private LocalDate commencementDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "emission_trading_scheme")
    @NotNull
    private EmissionTradingScheme emissionTradingScheme;

    @Column(name = "accepted_date")
    private LocalDateTime acceptedDate;

    @Column(name = "verification_body_id")
    private Long verificationBodyId;

    @Builder.Default
    @ElementCollection
    @MapKeyColumn(name="contact_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name="user_id")
    @CollectionTable(name = "account_contact", joinColumns = @JoinColumn(name = "account_id"))
    private Map<AccountContactType, String> contacts = new EnumMap<>(AccountContactType.class);

    @Column(name = "migrated_account_id")
    private String migratedAccountId;

    @EqualsAndHashCode.Include()
    @Column(name = "emitter_id", unique = true)
    @NotBlank
    @Size(min = 7, max = 7)
    private String emitterId;

    @Column(name = "registry_id")
    @Min(1000000)
    @Max(9999999)
    private Integer registryId;

    @Column(name = "sop_id")
    @Min(0)
    @Max(9999999999L)
    private Long sopId;

    public abstract AccountStatus getStatus();
}
