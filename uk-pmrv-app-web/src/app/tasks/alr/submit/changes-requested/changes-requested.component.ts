import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-alr-submit-changes-requested',
  imports: [AlrTaskSharedModule, ChangesRequestedTemplateComponent, SharedModule],
  templateUrl: './changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrChangesRequestedComponent {
  payload = this.alrService.payload as Signal<ALRApplicationAmendsSubmitRequestTaskPayload>;
  isEditable = this.alrService.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.alrSectionsCompleted?.changesRequested
        ? [this.payload()?.alrSectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  constructor(
    readonly alrService: AlrService,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.alrService
        .postTaskSave(null, null, true, 'changesRequested')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    }
  }
}
