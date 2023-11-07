import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-submit',
  templateUrl: './submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class SubmitComponent {
  allowSubmit$ = this.store.pipe(map((state) => state.sectionsCompleted?.SURRENDER_APPLY));
  isPermitSurrenderSubmitted$ = new BehaviorSubject(false);
  competentAuthority$ = this.store.select('competentAuthority');

  constructor(readonly store: PermitSurrenderStore, readonly pendingRequest: PendingRequestService) {}

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap(() => this.store.postSubmitPermitSurrender()),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.isPermitSurrenderSubmitted$.next(true);
      });
  }
}
