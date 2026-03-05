import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { BehaviorSubject, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core/hseti.service';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

@Component({
  selector: 'app-send-report',
  templateUrl: './send-report.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, RouterLink, HseTiTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SendReportComponent implements PendingRequest {
  isSubmitted$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  requestId$: BehaviorSubject<string | null> = new BehaviorSubject(null);
  isSendReportAvailable$ = this.hseTiService.payload$.pipe(
    map((payload) => payload?.hsetiSectionsCompleted?.['details'] === true),
  );
  allocationPeriod = this.hseTiService.allocationPeriod;
  isEditable$ = this.hseTiService.isEditable$;
  title: Signal<string> = computed(() => `${this.allocationPeriod()} HSE target increase submitted`);

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: CommonTasksStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly hseTiService: HseTiService,
  ) {}

  onSubmit() {
    this.store.requestTaskType$
      .pipe(
        first(),
        map((requestTaskType) => {
          let actionType;

          switch (requestTaskType) {
            case 'HSE_TI_APPLICATION_SUBMIT':
              actionType = 'HSE_TI_SUBMIT_TO_REGULATOR';
              break;
            case 'HSE_TI_APPLICATION_AMENDS_SUBMIT':
              actionType = 'HSE_TI_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR';
              break;
          }

          return actionType;
        }),
        switchMap((actionType) => this.hseTiService.postHseTiSubmit(actionType)),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.router.navigate(['../confirmation'], { relativeTo: this.route, queryParams: { sendTo: 'regulator' } });
      });
  }
}
