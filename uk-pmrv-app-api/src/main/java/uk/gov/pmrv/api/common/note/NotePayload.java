package uk.gov.pmrv.api.common.note;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotePayload {

    @NotEmpty
    @Size(max = 10000)
    private String note;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<UUID, String> files = new HashMap<>();
}
