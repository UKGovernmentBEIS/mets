import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BdrService, BdrTaskSharedModule } from '@tasks/bdr/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';

@Component({
  selector: 'app-bdr-changes-requested',
  templateUrl: './bdr-changes-requested.component.html',
  standalone: true,
  imports: [SharedModule, TaskSharedModule, BdrTaskSharedModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BdrChangesRequestedComponent {
  payload = this.bdrService.payload as Signal<BDRApplicationAmendsSubmitRequestTaskPayload>;

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.bdrSectionsCompleted?.changesRequested
        ? [this.payload()?.bdrSectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  isEditable = this.bdrService.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);
  constructor(
    readonly bdrService: BdrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    readonly pendingRequest: PendingRequestService,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.bdrService
        .postTaskSave({}, {}, true, `changesRequested`)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    }
  }
}
