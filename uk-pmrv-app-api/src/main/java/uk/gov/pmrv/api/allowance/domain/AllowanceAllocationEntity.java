package uk.gov.pmrv.api.allowance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

import java.time.Year;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "allowance_allocation",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"account_id", "year", "sub_installation_name"}))
public class AllowanceAllocationEntity {

    @Id
    @SequenceGenerator(name = "allowance_allocation_id_generator", sequenceName = "allowance_allocation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allowance_allocation_id_generator")
    private Long id;

    @EqualsAndHashCode.Include
    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @EqualsAndHashCode.Include
    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year year;

    @EqualsAndHashCode.Include
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_installation_name")
    @NotNull
    private SubInstallationName subInstallationName;

    @Column(name = "allocation")
    @PositiveOrZero
    @NotNull
    private Integer allocation;
}
