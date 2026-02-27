import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';
import { BdrS2Service } from '@tasks/bdrs2/core';
import { BdrS2TaskSharedModule } from '@tasks/bdrs2/shared';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { GovukValidators } from 'govuk-components';

import { BDRS2ApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-bdrs2-changes-requested',
  imports: [SharedModule, TaskSharedModule, BdrS2TaskSharedModule, ChangesRequestedTemplateComponent],
  templateUrl: './changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Bdrs2ChangesRequestedComponent {
  payload = this.bdrs2Service.payload as Signal<BDRS2ApplicationAmendsSubmitRequestTaskPayload>;

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.bdrs2SectionsCompleted?.changesRequested
        ? [this.payload()?.bdrs2SectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  isEditable = this.bdrs2Service.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);
  constructor(
    readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
    readonly pendingRequest: PendingRequestService,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.bdrs2Service
        .postTaskSave({}, {}, true, `changesRequested`)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    }
  }
}
