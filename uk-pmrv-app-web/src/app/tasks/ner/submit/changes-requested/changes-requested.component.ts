import { ChangeDetectionStrategy, Component, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';
import { NerService } from '@tasks/ner/core';
import { NerTaskComponent } from '@tasks/ner/shared';

import { GovukValidators } from 'govuk-components';

import { NERApplicationAmendsSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-ner-submit-changes-requested',
  imports: [ChangesRequestedTemplateComponent, SharedModule, NerTaskComponent],
  templateUrl: './changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NerChangesRequestedComponent {
  payload = this.nerService.payload as Signal<NERApplicationAmendsSubmitRequestTaskPayload>;
  isEditable = this.nerService.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.nerSectionsCompleted?.changesRequested
        ? [this.payload()?.nerSectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  constructor(
    readonly nerService: NerService,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
  ) {}

  confirm() {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.nerService
        .postTaskSave(null, null, true, 'changesRequested')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    }
  }
}
