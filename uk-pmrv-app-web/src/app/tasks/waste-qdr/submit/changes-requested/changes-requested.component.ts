import { ChangeDetectionStrategy, Component, computed, Signal } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { ChangesRequestedTemplateComponent } from '@shared/components/changes-requested-template/changes-requested-template.component';
import { SharedModule } from '@shared/shared.module';
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskComponent } from '@tasks/waste-qdr/shared';

import { GovukValidators } from 'govuk-components';

import {
  WasteQDRApplicationAmendsSubmitRequestTaskPayload,
  WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
} from 'pmrv-api';

interface ViewModel {
  isEditable: boolean;
  decisionDetails: WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails;
  regulatorReviewAttachments: { [key: string]: string };
  downloadUrl: string;
}

@Component({
  selector: 'app-waste-qdr-changes-requested',
  imports: [ChangesRequestedTemplateComponent, SharedModule, WasteQdrTaskComponent],
  standalone: true,
  templateUrl: './changes-requested.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrChangesRequestedComponent {
  payload = this.wasteQdrService.payload as Signal<WasteQDRApplicationAmendsSubmitRequestTaskPayload>;
  isEditable = this.wasteQdrService.isEditable;
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  vm: Signal<ViewModel> = computed(() => {
    const isEditable = this.isEditable();
    const {
      regulatorReviewAttachments,
      reviewDecision: { details },
    } = this.payload();

    return {
      isEditable,
      regulatorReviewAttachments,
      decisionDetails: details as WasteQDRRegulatorReviewOperatorAmendsNeededDecisionDetails,
      downloadUrl: this.wasteQdrService.getBaseFileDownloadUrl(),
    };
  });

  form: UntypedFormGroup = this.fb.group({
    changesRequested: [
      this.payload()?.wasteQDRSectionsCompleted?.changesRequested
        ? [this.payload()?.wasteQDRSectionsCompleted?.changesRequested]
        : null,
      GovukValidators.required('Check the box to confirm you have made changes and want to mark as complete'),
    ],
  });

  constructor(
    readonly wasteQdrService: WasteQdrService,
    private readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: UntypedFormBuilder,
  ) {}

  onSubmit() {
    if (this.form.valid) {
      this.wasteQdrService
        .postTaskSave(null, null, true, 'changesRequested')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../'], { relativeTo: this.route }));
    } else {
      this.displayErrorSummary$.next(true);
    }
  }
}
