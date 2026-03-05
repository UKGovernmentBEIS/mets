import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { SharedModule } from '@shared/shared.module';

import { DigitizedPlan } from 'pmrv-api';

import { AvoidDoubleCountFormProvider } from './avoid-double-count-form.provider';

@Component({
  selector: 'app-mmp-avoid-double-count',
  standalone: true,
  imports: [SharedPermitModule, SharedModule],
  templateUrl: './avoid-double-count.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [AvoidDoubleCountFormProvider],
})
export class AvoidDoubleCountComponent {
  permitTask = toSignal(this.route.data.pipe(map((x) => x?.permitTask)));
  state = toSignal(this.store.pipe());

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit() {
    const permitTask = this.permitTask();
    const monitoringMethodologyPlans = this.state().permit.monitoringMethodologyPlans;

    this.store
      .patchTask(
        permitTask,
        {
          ...monitoringMethodologyPlans,
          digitizedPlan: {
            ...monitoringMethodologyPlans?.digitizedPlan,
            methodTask: {
              ...monitoringMethodologyPlans?.digitizedPlan?.methodTask,
              avoidDoubleCount: this.form.value.avoidDoubleCount,
              avoidDoubleCountToggle: true,
            },
          } as DigitizedPlan,
        },
        false,
        'mmpMethods',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route }));
  }
}
