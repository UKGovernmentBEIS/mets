import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { OnlyAnswersSectionComponent } from '@permit-application/shared/only-answers-section/only-answers-section.component';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-mmp-installation-description-answers',
  templateUrl: './mmp-installation-description-answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MmpInstallationDescriptionAnswersComponent extends OnlyAnswersSectionComponent implements PendingRequest {
  hideSubmit$ = this.store.isEditable$.pipe(
    map(
      (isEditable) => !isEditable || this.store.getState().permitSectionsCompleted?.['mmpInstallationDescription']?.[0],
    ),
  );

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly router: Router,

    readonly pendingRequest: PendingRequestService,
    readonly route: ActivatedRoute,
  ) {
    super(store, router, route);
  }

  onSubmit() {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus('mmpInstallationDescription', true, data.permitTask)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateSubmitSection('../..', 'monitoring-methodology-plan'));
  }
}
