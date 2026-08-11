import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { CustomReportPreview } from '../../core/custom-report';

@Component({
  selector: 'app-custom-report-preview',
  standalone: false,
  templateUrl: './custom-report-preview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CustomReportPreviewComponent {
  @Input() preview: CustomReportPreview | null = null;
}
