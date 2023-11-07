import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { extractMonitorinApproachType } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { empHeaderTaskMap } from '../../../shared/util/emp.util';
import { DataGapsFormModel } from './data-gaps-form.model';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection, @angular-eslint/use-component-selector
@Component({
  template: ``,
})
export class BaseDataGapsComponent implements OnInit, OnDestroy {
  readonly empHeaderTaskMap = empHeaderTaskMap;
  protected readonly _form = inject<DataGapsFormModel>(TASK_FORM_PROVIDER);
  protected readonly fb = inject(FormBuilder);
  protected readonly store = inject(RequestTaskStore);
  protected readonly pendingRequestService = inject(PendingRequestService);
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);
  protected readonly backLinkService = inject(BackLinkService);

  ngOnInit(): void {
    this.backLinkService.show();
  }
  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
  protected get isCert(): boolean {
    const monitoringApproachType = extractMonitorinApproachType(this.store.empDelegate.payload.emissionsMonitoringPlan);
    if (!monitoringApproachType) throw new Error('no monitoringApproachType selected while inside data gaps.');
    return monitoringApproachType === 'CERT_MONITORING';
  }
}
