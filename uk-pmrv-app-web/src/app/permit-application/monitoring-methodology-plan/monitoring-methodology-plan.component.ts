import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mmpStatuses } from './mmp-status';
import { monitoringMethodologyPlanAddFormFactory } from './monitoring-methodology-plan-form.provider';

@Component({
  selector: 'app-monitoring-methodology-plan',
  templateUrl: './monitoring-methodology-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [monitoringMethodologyPlanAddFormFactory],
})
export class MonitoringMethodologyPlanComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['upload-file'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.store
              .postTask(
                data.permitTask,
                this.form.value.exist ? this.form.value : { exist: this.form.value.exist, plans: null },
                false,
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
          switchMap(() => this.store),
          first(),
          tap((state) => {
            const newInitialPermitSections = { ...state.permitSectionsCompleted };
            delete newInitialPermitSections?.['MMP_SUB_INSTALLATION_Fallback_Approach'];
            delete newInitialPermitSections?.['MMP_SUB_INSTALLATION_Product_Benchmark'];
            this.store.setState({
              ...state,
              permitSectionsCompleted:
                !state.permit.monitoringMethodologyPlans?.exist && state?.features?.['digitized-mmp']
                  ? mmpStatuses.reduce((acc, section) => ({ ...acc, [section]: [false] }), {
                      ...newInitialPermitSections,
                    })
                  : state.permitSectionsCompleted,
            });
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['upload-file'], { relativeTo: this.route }));
    }
  }
}
