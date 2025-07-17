package uk.gov.pmrv.api.mireport.common;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.mireport.domain.MiReportEntity;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class PmrvMiReportEntity extends MiReportEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;
}
