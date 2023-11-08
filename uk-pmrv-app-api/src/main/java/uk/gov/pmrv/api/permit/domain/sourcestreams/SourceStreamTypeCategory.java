package uk.gov.pmrv.api.permit.domain.sourcestreams;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceStreamTypeCategory {
    private SourceStreamType type;

    @JsonProperty("category")
    public SourceStreamCategory getSourceStreamCategory() {
        return type.getCategory();
    }
}
