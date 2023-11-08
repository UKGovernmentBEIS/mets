package uk.gov.pmrv.api.token;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileToken {

    private String token;
    private long tokenExpirationMinutes;
    
}
