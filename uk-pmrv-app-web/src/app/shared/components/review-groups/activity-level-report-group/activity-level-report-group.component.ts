import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { ActivityLevelReport } from 'pmrv-api';

@Component({
  selector: 'app-activity-level-report-group',
  templateUrl: './activity-level-report-group.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ActivityLevelReportGroupComponent {
  @Input() activityLevelReport: ActivityLevelReport;
  @Input() documentFiles: { downloadUrl: string; fileName: string }[];
  @Input() isEditable = false;
}
