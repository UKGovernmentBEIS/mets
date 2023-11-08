package uk.gov.pmrv.api.competentauthority.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "competent_authority")
public class CompetentAuthority {

	@Id
	@Enumerated(EnumType.STRING)
	@EqualsAndHashCode.Include()
	private CompetentAuthorityEnum id;

	@Column(name = "email")
	private String email;

	@Column(name = "aviation_email")
	private String aviationEmail;

	@Column(name = "name")
	private String name;
}
