package uk.gov.pmrv.api.mireport.userdefined;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Entity
@Data
@Table(name = "mi_report_user_defined_account_type")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MiReportUserDefinedAccountType {

    @Id
    @Column(name = "mi_report_id")
    private Long miReportId;

    @Enumerated(EnumType.STRING)
    @Column(name="account_type")
    private AccountType accountType;
}
