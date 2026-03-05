import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import {
  AerApplicationCompletedRequestActionPayload,
  AerApplicationSubmittedRequestActionPayload,
  AerApplicationVerificationSubmittedRequestActionPayload,
} from 'pmrv-api';

@Component({
  selector: 'app-operator-submitted-template',
  templateUrl: './operator-submitted-template.component.html',
  styleUrl: './operator-submitted-template.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorSubmittedTemplateComponent {
  @Input() payload:
    | AerApplicationSubmittedRequestActionPayload
    | AerApplicationCompletedRequestActionPayload
    | AerApplicationVerificationSubmittedRequestActionPayload;
  @Input() additionalDocumentFiles: { downloadUrl: string; fileName: string }[];
  @Input() activityLevelReportFiles: { downloadUrl: string; fileName: string }[];
  @Input() sourcesColumns;
  @Input() pointsColumns;
}
