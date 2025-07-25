import { ActivatedRoute, Router } from '@angular/router';

import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { reviewRequestTaskTypes } from '../utils/permit';

export abstract class OnlyAnswersSectionComponent {
  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,
    readonly route: ActivatedRoute,
  ) {}

  navigateSubmitSection(target: string, reviewGroupUrl: string): void {
    let url = target;
    if (reviewRequestTaskTypes.includes(this.store.getState().requestTaskType)) {
      url = `../../review/${reviewGroupUrl}`;
    }
    this.router.navigate([url], { relativeTo: this.route, state: { notification: true } });
  }
}
