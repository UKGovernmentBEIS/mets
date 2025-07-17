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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.netz.api.common.config.YearAttributeConverter;
import uk.gov.pmrv.api.allowance.domain.enums.ChangeType;
import uk.gov.pmrv.api.allowance.domain.enums.SubInstallationName;

import java.time.LocalDateTime;
import java.time.Year;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "allowance_activity_level")
public class AllowanceActivityLevelEntity {

    @Id
    @SequenceGenerator(name = "allowance_activity_level_id_generator", sequenceName = "allowance_activity_level_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "allowance_activity_level_id_generator")
    private Long id;

    @Column(name = "account_id")
    @NotNull
    private Long accountId;

    @Column(name = "year")
    @Convert(converter = YearAttributeConverter.class)
    @NotNull
    private Year year;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_installation_name")
    @NotNull
    private SubInstallationName subInstallationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type")
    @NotNull
    private ChangeType changeType;

    @Column(name = "other_change_type_name")
    private String otherChangeTypeName;

    @Column(name = "amount")
    private String amount;

    @Column(name = "comments")
    @NotBlank
    private String comments;

    @NotNull
    @Column(name = "creation_date")
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}
