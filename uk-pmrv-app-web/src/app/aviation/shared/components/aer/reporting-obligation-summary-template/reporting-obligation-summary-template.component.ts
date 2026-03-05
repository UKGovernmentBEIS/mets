import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-reporting-obligation-summary-template',
  templateUrl: './reporting-obligation-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportingObligationSummaryTemplateComponent {
  @Input() reportingData: ReportingObligation;
  @Input() year: number;
  @Input() files: { fileName: string; downloadUrl: string }[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
