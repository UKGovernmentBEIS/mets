import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaConfidentiality } from 'pmrv-api';

@Component({
  selector: 'app-confidentiality-summary-template',
  templateUrl: './confidentiality-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfidentialitySummaryTemplateComponent {
  @Input() confidentialityData: AviationAerCorsiaConfidentiality;
  @Input() totalEmissionsFiles: { fileName: string; downloadUrl: string }[];
  @Input() aggregatedStatePairDataFiles: { fileName: string; downloadUrl: string }[];
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
