import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { AviationAerCorsiaMonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/monitoring-approach-form.provider';
import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { getSummaryHeaderForTaskType } from '@aviation/request-task/util';
import { MonitoringApproachCorsiaSummaryTemplateComponent } from '@aviation/shared/components/aer/monitoring-approach-corsia-summary-template';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

export interface ViewModel {
  data: AviationAerCorsiaMonitoringApproach | null;
  pageHeader: string;
  isEditable: boolean;
  hideSubmit: boolean;
}
@Component({
  selector: 'app-monitoring-approach-summary',
  templateUrl: './monitoring-approach-summary.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, MonitoringApproachCorsiaSummaryTemplateComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class MonitoringApproachSummaryComponent {
  private formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  form = this.formProvider.form;

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestTaskType),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerQuery.selectStatusForTask('monitoringApproach')),
  ]).pipe(
    map(([type, isEditable, taskStatus]) => {
      return {
        data: this.form.valid ? (this.formProvider.form.value as AviationAerCorsiaMonitoringApproach) : null,
        pageHeader: getSummaryHeaderForTaskType(type, 'monitoringApproach'),
        isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  onSubmit() {
    if (this.form?.valid) {
      (this.store.aerDelegate as AerCorsiaStoreDelegate)
        .saveAer(
          { monitoringApproach: this.formProvider.form.value as AviationAerCorsiaMonitoringApproach },
          'complete',
        )
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['../../../'], { relativeTo: this.route });
        });
    }
  }
}
