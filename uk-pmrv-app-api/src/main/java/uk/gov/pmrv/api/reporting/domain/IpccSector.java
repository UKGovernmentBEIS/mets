package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rpt_ipcc_sector")
public class IpccSector {

    @Id
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "name", unique = true)
    @NotNull
    private String name;

    @Column(name = "display_name")
    @NotNull
    private String displayName;
}
