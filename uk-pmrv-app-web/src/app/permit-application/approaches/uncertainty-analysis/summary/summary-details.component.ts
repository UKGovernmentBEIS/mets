import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { iif } from 'rxjs';

import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-uncertainty-analysis-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() showOriginal = false;
  @Input() isPreview: boolean;
  @Input() hasBottomBorder = true;

  readonly files$ = iif(
    () => this.showOriginal,
    this.store.findOriginalTask('uncertaintyAnalysis.attachments'),
    this.store.findTask('uncertaintyAnalysis.attachments'),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}
