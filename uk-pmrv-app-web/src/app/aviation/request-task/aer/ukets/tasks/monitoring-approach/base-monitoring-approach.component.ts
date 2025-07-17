import { Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

import { AerMonitoringApproachFormProvider } from './monitoring-approach-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
/* eslint-disable @angular-eslint/use-component-selector */
@Component({
  template: '',
})
export class BaseMonitoringApproachComponent {
  constructor(
    @Inject(TASK_FORM_PROVIDER) protected formProvider: AerMonitoringApproachFormProvider,
    protected store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    protected backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  /* eslint-disable @typescript-eslint/no-empty-function */
  protected saveEmpAndNavigate(
    data: AviationAerEmissionsMonitoringApproach,
    status: TaskItemStatus,
    navigationUrl: string,
    done: () => void = () => {},
  ) {
    this.store.aerDelegate
      .saveAer(
        {
          monitoringApproach: data,
        },
        status,
      )
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        done();
        (this.store.aerDelegate as AerUkEtsStoreDelegate).setMonitoringApproach(data);
        const navigation = {
          relativeTo: this.route,
        } as any;
        this.router.navigate([navigationUrl], navigation);
      });
  }
}
