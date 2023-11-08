package uk.gov.pmrv.api.workflow.request.core.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.note.Note;

@Entity
@SequenceGenerator(name = "default_note_id_generator", sequenceName = "request_note_seq", allocationSize = 1)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "request_note")
public class RequestNote extends Note {

    @Column(name = "request_id")
    private String requestId;
}
