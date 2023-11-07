import { inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { MonitoringApproachFormProvider } from './monitoring-approach-form.provider';

export class BaseMonitoringApproachComponent {
  formProvider = inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  protected readonly store = inject(RequestTaskStore);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);
  protected readonly pendingRequestService = inject(PendingRequestService);
  protected backLinkService = inject(BackLinkService);

  /* eslint-disable @typescript-eslint/no-empty-function */
  protected saveEmpAndNavigate(
    data: EmpEmissionsMonitoringApproach,
    status: TaskItemStatus,
    navigationUrl: string,
    done: () => void = () => {},
  ) {
    this.store.empUkEtsDelegate
      .saveEmp({ emissionsMonitoringApproach: data } as any, status)
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        done();
        this.store.empUkEtsDelegate.setEmissionsMonitoringApproach(data as any);
        this.router.navigate([navigationUrl], {
          relativeTo: this.route,
        });
      });
  }
}
