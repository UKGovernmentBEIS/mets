import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';

import { reviewRequestTaskTypes } from '../../../../shared/utils/permit';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AnswersComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  confirm(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus(data.statusKey, true).pipe(this.pendingRequest.trackRequest())),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [reviewRequestTaskTypes.includes(state.requestTaskType) ? '../../../review/calculation' : '../summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}
