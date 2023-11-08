package uk.gov.pmrv.api.account.aviation.domain;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "account_aviation_reporting_status_history")
public class AviationAccountReportingStatusHistory {

    @Id
    @SequenceGenerator(name = "account_aviation_reporting_status_history_id_generator", sequenceName = "account_aviation_reporting_status_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_aviation_reporting_status_history_id_generator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AviationAccountReportingStatus status;

    private String reason;

    @NotBlank
    private String submitterId;

    @NotBlank
    private String submitterName;

    @NotNull
    @CreatedDate
    private LocalDateTime submissionDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private AviationAccount account;
}
