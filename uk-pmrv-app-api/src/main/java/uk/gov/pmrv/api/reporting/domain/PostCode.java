package uk.gov.pmrv.api.reporting.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "rpt_post_code")
public class PostCode {

    @Id
    @SequenceGenerator(name = "rpt_post_code_id_generator", sequenceName = "rpt_post_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rpt_post_code_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charging_zone_id")
    private ChargingZone chargingZone;

    @EqualsAndHashCode.Include()
    @Column(name = "code")
    @NotBlank
    private String code;
}
