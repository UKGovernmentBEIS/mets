import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, Observable, pluck, switchMap } from 'rxjs';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { headingMap } from '../heading';
import { pipelineFormProvider } from './pipeline-form.provider';

@Component({
  selector: 'app-pipeline',
  templateUrl: './pipeline.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [pipelineFormProvider],
})
export class PipelineComponent {
  taskKey$: Observable<string> = this.route.data.pipe(pluck('taskKey'));
  headingMap = headingMap;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(): void {
    combineLatest([this.route.data, this.store.getTask('monitoringApproaches')])
      .pipe(
        first(),
        switchMap(([data, monitoringApproaches]) =>
          this.store.patchTask(
            data.taskKey,
            this.form.value.exist
              ? {
                  ...(monitoringApproaches?.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach)
                    .transportCO2AndN2OPipelineSystems,
                  exist: this.form.value.exist,
                }
              : {
                  exist: this.form.value.exist,
                  procedureForLeakageEvents: null,
                  proceduresForTransferredCO2AndN2O: null,
                  temperaturePressure: null,
                },
            false,
            data.statusKey,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() =>
        this.router.navigate([this.form.value.exist ? 'leakage' : 'answers'], { relativeTo: this.route }),
      );
  }
}
