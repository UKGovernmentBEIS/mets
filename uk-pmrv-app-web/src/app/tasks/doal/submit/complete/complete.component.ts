import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, map } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DoalService } from '../../core/doal.service';

@Component({
  selector: 'app-complete',
  templateUrl: './complete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompleteComponent {
  confirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly requestId$ = this.doalService.requestTaskItem$.pipe(
    map((requestTaskItem) => requestTaskItem.requestInfo.id),
  );

  constructor(
    readonly doalService: DoalService,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  complete(): void {
    this.doalService
      .complete()
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.confirmationDisplayed$.next(true));
  }
}
