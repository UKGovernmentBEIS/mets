package uk.gov.pmrv.api.web.orchestrator.account.installation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.files.common.domain.dto.FileInfoDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InstallationAccountDetailsDTO {

    private InstallationAccountPermitDTO accountPermitDto;
    private FileInfoDTO latestAlrFile;
}
