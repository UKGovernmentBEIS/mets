import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { SummaryDetailsAbstractComponent } from '../../review/determination/summary/summary-details-abstract.component';

@Component({
  selector: 'app-permit-application-determination-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewDeterminationSummaryDetailsComponent extends SummaryDetailsAbstractComponent {
  @Input() cssClass: string;
  @Input() changePerStage: boolean;

  constructor(
    protected readonly store: PermitApplicationStore<PermitApplicationState>,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }
}
