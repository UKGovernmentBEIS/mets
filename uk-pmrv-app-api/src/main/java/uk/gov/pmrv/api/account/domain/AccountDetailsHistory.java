package uk.gov.pmrv.api.account.domain;

import com.fasterxml.jackson.databind.JsonNode;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.Type;
import uk.gov.pmrv.api.account.domain.enumeration.AccountDetailsHistoryCategory;

import java.time.LocalDateTime;

@Entity
@SequenceGenerator(name = "account_details_history_generator", sequenceName = "account_note_seq", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "account_details_history")
@Data
public class AccountDetailsHistory {

    @Id
    @SequenceGenerator(name = "account_details_history_id_generator", sequenceName = "account_details_history_seq",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_details_history_id_generator")
    private Long id;

    @Column(name = "account_id")
    @NotNull
    @EqualsAndHashCode.Include()
    private Long accountId;

    @NotNull
    @Column(name = "changed_by")
    private String changedBy;

    @NotNull
    @Column(name = "creation_date")
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "category")
    private AccountDetailsHistoryCategory category;

    @Type(JsonBinaryType.class)
    @NotNull
    @Column(columnDefinition = "jsonb", name = "previous_value")
    private JsonNode previousValue;

    @Type(JsonBinaryType.class)
    @NotNull
    @Column(columnDefinition = "jsonb",name = "new_value")
    private JsonNode newValue;

    @Column(name = "reason")
    private String reason;

}
