package uk.gov.pmrv.api.migration.activitylevelchange;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLevelChangeVO {

    private String emitterId;
    private String emitterDisplayId;
    private LocalDateTime endDate;
    private Year historyYear;
    private String subInstallationName;
    private String changeType;
    private String changedActivityLevel;
    private String comments;
}
