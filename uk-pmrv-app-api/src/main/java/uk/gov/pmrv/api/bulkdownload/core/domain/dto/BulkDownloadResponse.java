package uk.gov.pmrv.api.bulkdownload.core.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkDownloadResponse {

    private String filename;
    private StreamingResponseBody body;
}