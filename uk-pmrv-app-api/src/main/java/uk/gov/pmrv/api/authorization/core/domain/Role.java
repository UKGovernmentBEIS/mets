package uk.gov.pmrv.api.authorization.core.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a role template
 *
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "au_role")
@NamedEntityGraph(
	name = "role-permissions-graph",
	attributeNodes = {  
		@NamedAttributeNode("rolePermissions")
	}
)
public class Role {

	@Id
	@SequenceGenerator(name = "au_role_id_generator", sequenceName = "au_role_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "au_role_id_generator")
	private Long id;
	
	@NotNull
	@Column(name = "name")
	private String name;

	@EqualsAndHashCode.Include()
	@NotNull
	@Column(name = "code", unique = true)
	private String code;
	
	/**
	 * The type of the role
	 */
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private RoleType type;

	@Builder.Default
	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RolePermission> rolePermissions = new ArrayList<>();
	
	/**
	 * Add the provided permission
	 * @param rolePermission {@link RolePermission}
	 */
	public void addPermission(RolePermission rolePermission) {
		if(rolePermissions == null) {
			rolePermissions = new ArrayList<>();
		}
		rolePermission.setRole(this);
		rolePermissions.add(rolePermission);
	}
}
