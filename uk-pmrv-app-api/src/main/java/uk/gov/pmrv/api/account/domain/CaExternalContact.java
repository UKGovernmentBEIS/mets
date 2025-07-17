package uk.gov.pmrv.api.account.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@EntityListeners({AuditingEntityListener.class})
@Table(name = "account_ca_external_contacts",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"competent_authority", "name"}),
        @UniqueConstraint(columnNames = {"competent_authority", "email"})})
public class CaExternalContact {

    @Id
    @SequenceGenerator(name = "ca_external_contacts_generator", sequenceName = "account_ca_external_contacts_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ca_external_contacts_generator")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "competent_authority")
    @NotNull
    private CompetentAuthorityEnum competentAuthority;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "email")
    @NotBlank
    private String email;

    @Column(name = "description")
    @NotBlank
    private String description;

    @NotNull
    @Column(name = "last_updated_date")
    @LastModifiedDate
    private LocalDateTime lastUpdatedDate;
}
