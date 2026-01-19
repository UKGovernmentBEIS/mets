package uk.gov.pmrv.api.account.aviation.domain;

import io.hypersistence.utils.hibernate.type.basic.YearType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "account_aviation_reporting_status")
public class AviationAccountReportingStatus {

    @Id
    @SequenceGenerator(name = "account_aviation_reporting_status_id_generator", sequenceName = "account_aviation_reporting_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_aviation_reporting_status_id_generator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AviationAccountReportingStatusType status;

    @Type(YearType.class)
    @Column(
            name = "year",
            columnDefinition = "smallint"
    )
    @NotNull
    @EqualsAndHashCode.Include()
    private Year year;

    @Column(name = "reason")
    private String reason;

    @LastModifiedDate
    @Column(name = "last_update", insertable = false)
    private LocalDateTime lastUpdate;

    @NotNull
    @EqualsAndHashCode.Include()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AviationAccount account;



}
