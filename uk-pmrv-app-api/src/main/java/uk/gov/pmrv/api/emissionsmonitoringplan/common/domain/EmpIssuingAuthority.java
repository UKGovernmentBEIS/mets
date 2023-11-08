package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "emp_issuing_authority")
@NamedQuery(
        name = EmpIssuingAuthority.NAMED_QUERY_FIND_ALL_ISSUING_AUTHORITY_NAMES,
        query = "select auth.name "
                + " from EmpIssuingAuthority auth "
                + " order by auth.name"
)
public class EmpIssuingAuthority {

    public static final String NAMED_QUERY_FIND_ALL_ISSUING_AUTHORITY_NAMES = "EmpIssuingAuthority.findAllIssuingAuthorityNames";

    @Id
    @SequenceGenerator(name = "emp_issuing_authority_id_generator", sequenceName = "emp_issuing_authority_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "emp_issuing_authority_id_generator")
    private Long id;

    /**
     * The issuing authority name.
     */
    @Column(name = "name", unique = true)
    @NotBlank
    private String name;

}
