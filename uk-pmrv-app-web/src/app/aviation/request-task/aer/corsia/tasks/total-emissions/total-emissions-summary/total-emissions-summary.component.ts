import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { TotalEmissionsSchemeCorsiaComponent } from '@aviation/request-task/aer/corsia/tasks/total-emissions/shared/total-emissions-scheme-corsia';
import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskModule } from '@aviation/request-task/request-task.module';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-total-emissions-summary',
  templateUrl: './total-emissions-summary.component.html',
  imports: [SharedModule, ReturnToLinkComponent, RequestTaskModule, TotalEmissionsSchemeCorsiaComponent],
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TotalEmissionsSummaryComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('totalEmissionsCorsia')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        pageHeader: getSummaryHeaderForTaskType(type, 'totalEmissionsCorsia'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  onSubmit() {
    this.store.aerDelegate
      .saveAer({ totalEmissionsCorsia: undefined }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['../../../'], { relativeTo: this.route });
      });
  }
}
