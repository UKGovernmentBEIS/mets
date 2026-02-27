import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-opinion-statement-site-verification-summary-template',
  imports: [SharedModule, RouterLinkWithHref],
  templateUrl: './opinion-statement-site-verification-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementSiteVerificationSummaryTemplateComponent {
  @Input() siteVisit: any;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
