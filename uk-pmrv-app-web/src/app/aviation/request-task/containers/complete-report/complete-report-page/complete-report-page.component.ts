import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLinkWithHref } from '@angular/router';

import { switchMap, take } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

@Component({
  selector: 'app-complete-report-page',
  templateUrl: './complete-report-page.component.html',
  standalone: true,
  imports: [SharedModule, RouterLinkWithHref, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class CompleteReportPageComponent {
  constructor(
    private readonly store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit() {
    this.store
      .pipe(
        take(1),
        requestTaskQuery.selectRequestTaskType,
        switchMap((requestTaskType) => {
          let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

          if (requestTaskType === 'AVIATION_AER_UKETS_APPLICATION_REVIEW') {
            actionType = 'AVIATION_AER_UKETS_COMPLETE_REVIEW';

            return this.store.aerDelegate.submitAer(actionType);
          } else {
            return;
          }
        }),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['complete-report-confirmation'], { relativeTo: this.route });
      });
  }
}
