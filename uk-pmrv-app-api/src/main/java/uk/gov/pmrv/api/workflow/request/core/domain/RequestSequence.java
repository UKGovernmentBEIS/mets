package uk.gov.pmrv.api.workflow.request.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "request_sequence")
@ToString
public class RequestSequence {

    @Id
    @SequenceGenerator(name = "request_sequence_id_generator", sequenceName = "request_sequence_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_sequence_id_generator")
    private Long id;

    @Version
    @Setter(AccessLevel.NONE)
    private long version;

    @EqualsAndHashCode.Include()
    @Column(name = "business_identifier")
    private String businessIdentifier;

    @EqualsAndHashCode.Include()
    @Enumerated(EnumType.STRING)
    private RequestType type;

    @Builder.Default
    private long sequence = 0L;

    public RequestSequence(RequestType type) {
    	this(null, type);
    }
    
    public RequestSequence(String businessIdentifier, RequestType type) {
        this.businessIdentifier = businessIdentifier;
        this.type = type;
    }

    public long incrementSequenceAndGet() {
        this.sequence++;
        return this.sequence;
    }
}
