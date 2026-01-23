package uk.gov.pmrv.api.reporting.domain.bdr;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "baseline_data_report_free_allocation")
public class BaselineDataReportFreeAllocation {

    @Id
    @EqualsAndHashCode.Include
    @SequenceGenerator(name = "baseline_data_report_free_allocation_id_generator", sequenceName = "baseline_data_report_free_allocation_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseline_data_report_free_allocation_id_generator")
    private Long id;

    @Column(name = "account_id", unique = true, nullable = false)
    @NotNull
    private Long accountId;

    @Column(name = "free_allocation")
    @NotNull
    private Boolean freeAllocation;

}
