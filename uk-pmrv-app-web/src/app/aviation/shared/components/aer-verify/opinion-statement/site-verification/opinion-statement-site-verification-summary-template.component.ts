import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Params, RouterLinkWithHref } from '@angular/router';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-opinion-statement-site-verification-summary-template',
  templateUrl: './opinion-statement-site-verification-summary-template.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, RouterLinkWithHref],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class OpinionStatementSiteVerificationSummaryTemplateComponent {
  @Input() siteVisit: any;
  @Input() isEditable = false;
  @Input() queryParams: Params = {};
}
