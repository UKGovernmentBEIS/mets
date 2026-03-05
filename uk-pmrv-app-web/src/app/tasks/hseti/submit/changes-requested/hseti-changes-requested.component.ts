import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { HseTiService } from '@tasks/hseti/core';
import { HseTiTaskSharedModule } from '@tasks/hseti/shared/hseti-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { GovukValidators } from 'govuk-components';

import { HSETIApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-hseti-changes-requested',
  templateUrl: './hseti-changes-requested.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, HseTiTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HsetiChangesRequestedComponent {
  payload = this.hsetiService.payload as Signal<HSETIApplicationAmendsSubmitRequestTaskPayload>;
  allocationPeriod = this.hsetiService.allocationPeriod;

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.hsetiSectionsCompleted?.changesRequested
        ? [this.payload()?.hsetiSectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  subTitle = computed(() => {
    const subTitle = `${this.allocationPeriod()} HSE target increase details`;
    return subTitle;
  });

  isEditable = this.hsetiService.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);
  constructor(
    readonly hsetiService: HseTiService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    readonly pendingRequest: PendingRequestService,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.hsetiService
        .postTaskSave({}, {}, true, `changesRequested`)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    }
  }
}
