import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { MonitoringApproachVerifyCorsiaTemplateComponent } from '@aviation/shared/components/aer-verify/monitoring-approach-verify-corsia-template/monitoring-approach-verify-corsia-template.component';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerCorsiaOpinionStatement } from 'pmrv-api';

interface ViewModel {
  heading: string;
  opinionStatement: AviationAerCorsiaOpinionStatement;
  totalEmissionsProvided: string;
  totalOffsetEmissionsProvided: string;
  isEditable: boolean;
  hideSubmit: boolean;
}

@Component({
  selector: 'app-summary',
  standalone: true,
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [SharedModule, ReturnToLinkComponent, MonitoringApproachVerifyCorsiaTemplateComponent],
})
export class SummaryComponent {
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyCorsiaQuery.selectPayload),
    this.store.pipe(aerVerifyCorsiaQuery.selectStatusForTask('opinionStatement')),
  ]).pipe(
    map(([isEditable, payload, taskStatus]) => {
      return {
        heading: 'Check your answers',
        opinionStatement: this.formProvider.form.valid ? this.formProvider.getFormValue() : null,
        totalEmissionsProvided: payload.totalEmissionsProvided,
        totalOffsetEmissionsProvided: payload.totalOffsetEmissionsProvided,
        isEditable: isEditable,
        hideSubmit: !isEditable || ['complete', 'cannot start yet'].includes(taskStatus),
      };
    }),
  );

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: MonitoringApproachFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  onSubmit() {
    (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate)
      .saveAerVerify({ opinionStatement: this.formProvider.getFormValue() }, 'complete')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() =>
        this.router.navigate(['../../..'], {
          relativeTo: this.route,
        }),
      );
  }
}
