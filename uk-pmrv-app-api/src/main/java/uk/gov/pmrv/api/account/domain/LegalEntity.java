package uk.gov.pmrv.api.account.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;

/**
 * The Legal Entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "account_legal_entity")
@NamedEntityGraph(name = "fetchLocationWithHoldingCompany", attributeNodes = {
	@NamedAttributeNode(value = "location"),
	@NamedAttributeNode(value = "holdingCompany")
})
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_FIND_ACTIVE_LEGAL_ENTITIES_BY_ACCOUNTS_ORDER_BY_NAME,
		query = "select distinct(le) "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where ac.id in :accountIds "
				+ "and le.status = 'ACTIVE' "
				+ "order by le.name" )
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_EXISTS_LEGAL_ENTITY_IN_ANY_OF_ACCOUNTS,
		query = "select count(le) > 0 "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where le.id = :leId "
				+ "and ac.id in :accountIds")
@NamedQuery(
		name = LegalEntity.NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY_NAME_IN_ANY_OF_ACCOUNTS,
		query = "select count(le) > 0 "
				+ "from Account ac "
				+ "join ac.legalEntity le "
				+ "where le.name = :leName "
				+ "and le.status = 'ACTIVE' "
				+ "and ac.id in :accountIds")
public class LegalEntity {

	public static final String NAMED_QUERY_FIND_ACTIVE_LEGAL_ENTITIES_BY_ACCOUNTS_ORDER_BY_NAME = "LegalEntity.findActiveLegalEntitiesByAccountsOrderByName";
	public static final String NAMED_QUERY_EXISTS_LEGAL_ENTITY_IN_ANY_OF_ACCOUNTS = "LegalEntity.existsLegalEntityInAnyOfAccounts";
	public static final String NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY_NAME_IN_ANY_OF_ACCOUNTS = "LegalEntity.existsActiveLegalEntityNameInAnyOfAccounts";

	/** The id. */
	@Id
	@SequenceGenerator(name = "legal_entity_id_generator", sequenceName = "account_legal_entity_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "legal_entity_id_generator")
	private Long id;

	/** The Legal Entity name. */
	@EqualsAndHashCode.Include()
	@Column(name = "name", unique = true)
	@NotBlank
	private String name;

	/** The {@link Location}. */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "location_id")
	@NotNull
	private LocationOnShore location;

	/** The {@link LegalEntityType}. */
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	@NotNull
	private LegalEntityType type;

	/** The companies house reference number. */
	@Column(name = "reference_number")
	private String referenceNumber;

	/** The explanation of why organisation does not have a companies house reference number. */
	@Column(name = "no_reference_number")
	private String noReferenceNumberReason;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	@NotNull
	private LegalEntityStatus status;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "holding_company_id")
	private HoldingCompany holdingCompany;

}
