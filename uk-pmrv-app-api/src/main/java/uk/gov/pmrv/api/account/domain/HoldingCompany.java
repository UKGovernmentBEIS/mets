package uk.gov.pmrv.api.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account_holding_company")
public class HoldingCompany {

    @Id
    @SequenceGenerator(name = "account_holding_company_id_generator", sequenceName = "account_holding_company_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_holding_company_id_generator")
    private Long id;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Embedded
    @NotNull
    @Valid
    private HoldingCompanyAddress address;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HoldingCompany)) {
            return false;
        }
        HoldingCompany other = (HoldingCompany) o;

        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
