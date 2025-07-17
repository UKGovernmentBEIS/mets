import { Component, Inject } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';

import { tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AviationDoECorsia } from 'pmrv-api';

import { DoeCorsiaEmissionsFormProvider } from './doe-corsia-emissions-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
@Component({
  template: '',
})
export class BaseDoeCorsiaEmissionsComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) protected formProvider: DoeCorsiaEmissionsFormProvider,
    protected store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  /* eslint-disable @typescript-eslint/no-empty-function */
  protected saveDoeAndNavigate(
    data: AviationDoECorsia,
    status: TaskItemStatus,
    navigationUrl: string,
    isSummaryPage = false,
    done: () => void = () => {},
  ) {
    this.store.doeDelegate
      .saveDoe(
        {
          doe: { ...data },
        },
        status,
      )
      .pipe(
        tap(() => {
          this.pendingRequestService.trackRequest();
          done();
          this.store.doeDelegate.setAviationDoe(data);
        }),
      )
      .subscribe(() => {
        this.formProvider.addSupportingDocuments();

        const navigation: NavigationExtras = {
          relativeTo: this.route,
        };
        if (isSummaryPage) {
          navigation.queryParams = { change: null };
        } else {
          navigation.queryParamsHandling = 'merge';
        }
        this.router.navigate([navigationUrl], navigation);
      });
  }
}
