package uk.gov.pmrv.api.competentauthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.competentauthority.CompetentAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class PmrvCompetentAuthority extends CompetentAuthority {

	@Column(name = "aviation_email")
	private String aviationEmail;

	@Column(name = "waste_email")
	private String wasteEmail;
}
