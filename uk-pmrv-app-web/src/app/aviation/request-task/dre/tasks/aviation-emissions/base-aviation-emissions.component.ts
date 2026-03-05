import { Component, Inject } from '@angular/core';
import { ActivatedRoute, NavigationExtras, Router } from '@angular/router';

import { tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AviationDre } from 'pmrv-api';

import { AviationEmissionsFormProvider } from './aviation-emissions-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
@Component({
  template: '',
})
export class BaseAviationEmissionsComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) protected formProvider: AviationEmissionsFormProvider,
    protected store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  /* eslint-disable @typescript-eslint/no-empty-function */
  protected saveDreAndNavigate(
    data: AviationDre,
    status: TaskItemStatus,
    navigationUrl: string,
    isSummaryPage = false,
    done: () => void = () => {},
  ) {
    this.store.dreDelegate
      .saveDre({ dre: data }, status)
      .pipe(
        tap(() => {
          this.pendingRequestService.trackRequest();
          done();
          this.store.dreDelegate.setAviationDre(data);
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
