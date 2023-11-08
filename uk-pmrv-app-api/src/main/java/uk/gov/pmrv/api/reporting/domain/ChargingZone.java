package uk.gov.pmrv.api.reporting.domain;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rpt_charging_zone")
public class ChargingZone {

    @Id
    @SequenceGenerator(name = "rpt_charging_zone_id_generator", sequenceName = "rpt_charging_zone_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpt_charging_zone_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    @Column(name = "code", unique=true)
    @NotBlank
    private String code;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "chargingZone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RegionalInventoryData> regionalInventoryData = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "chargingZone", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostCode> postCodes = new ArrayList<>();
}
