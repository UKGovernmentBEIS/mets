package uk.gov.pmrv.api.account.aviation.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.Account;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "account_aviation")
@NamedEntityGraph(name = "reporting-status-history-graph",
        attributeNodes = {
                @NamedAttributeNode(value = "reportingStatusHistoryList")
        })
@SqlResultSetMapping(
        name = AviationAccount.AVIATION_ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER,
        classes = {
                @ConstructorResult(
                        targetClass = AviationAccountSearchResultsInfoDTO.class,
                        columns = {
                                @ColumnResult(name = "id", type = Long.class),
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "emitterId"),
                                @ColumnResult(name = "status", type = String.class)
                        }
                )})
public class AviationAccount extends Account {

    public static final String AVIATION_ACCOUNT_SEARCH_RESULTS_INFO_DTO_RESULT_MAPPER = "AviationAccountSearchResultsInfoDTOResultMapper";

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @NotNull
    private AviationAccountStatus status;
    
    @Column(name = "closing_date")
    private LocalDateTime closingDate;
    
    @Column(name = "closed_by")
    private String closedBy;
    
    @Column(name = "closed_by_name")
    private String closedByName;
    
    @Column(name = "closure_reason")
    private String closureReason;

    @Column(name = "central_route_charges_office_code")
    @NotBlank
    private String crcoCode;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "created_by")
    private String createdByUserId;

    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    @Column(name = "last_updated_by")
    private String updatedBy;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AviationAccountReportingStatus reportingStatus;

    @Builder.Default
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("submissionDate desc")
    private List<AviationAccountReportingStatusHistory> reportingStatusHistoryList = new ArrayList<>();

    public void addReportingStatusHistory(AviationAccountReportingStatusHistory reportingStatusHistory) {
    	reportingStatusHistory.setAccount(this);
        this.reportingStatusHistoryList.add(reportingStatusHistory);
    }
}
