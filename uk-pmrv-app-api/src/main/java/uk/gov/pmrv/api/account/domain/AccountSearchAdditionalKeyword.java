package uk.gov.pmrv.api.account.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "account_search_additional_keyword")
public class AccountSearchAdditionalKeyword {

    @Id
    @SequenceGenerator(name = "account_search_additional_keyword_id_generator", sequenceName = "account_search_additional_keyword_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_search_additional_keyword_id_generator")
    private Long id;

    @EqualsAndHashCode.Include()
    private Long accountId;

    @EqualsAndHashCode.Include()
    private String value;
}
