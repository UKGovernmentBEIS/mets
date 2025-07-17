import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { MmpInstallationDescriptionProvider } from './mmp-installation-description-form.provider';

@Component({
  selector: 'app-mmp-installation-description',
  templateUrl: './mmp-installation-description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [MmpInstallationDescriptionProvider],
})
export class MmpInstallationDescriptionComponent implements PendingRequest {
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    combineLatest([this.permitTask$, this.store])
      .pipe(
        first(),
        switchMap(([permitTask, state]) =>
          this.store.patchTask(
            permitTask,
            {
              ...state.permit.monitoringMethodologyPlans,
              digitizedPlan: {
                ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                installationDescription: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan?.installationDescription,
                  description: this.form.value.description,
                },
              },
            },
            false,
            'mmpInstallationDescription',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['upload-file'], { relativeTo: this.route }));
  }
}
