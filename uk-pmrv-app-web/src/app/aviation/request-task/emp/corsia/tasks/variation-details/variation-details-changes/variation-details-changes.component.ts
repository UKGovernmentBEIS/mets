import { ChangeDetectionStrategy, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { getChangesFormData } from '@aviation/request-task/emp/corsia/tasks/variation-details/util/variation-details-changes.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import {
  isReasonWizardRequired,
  materialChanges,
  nonMaterialChanges,
  otherChanges,
} from '@aviation/shared/components/emp-corsia/variation-details-summary-template/util/variation-details';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { EmpVariationCorsiaDetails } from 'pmrv-api';

import { checkboxSectionsValidator, VariationDetailsFormProvider } from '../variation-details-form.provider';

@Component({
  selector: 'app-variation-details-changes',
  standalone: true,
  imports: [RouterModule, SharedModule, ReturnToLinkComponent],
  templateUrl: './variation-details-changes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VariationDetailsChangesComponent implements OnInit, OnDestroy {
  form = new FormGroup(
    {
      reason: this.formProvider.variationDetailsReasonCtrl,
      materialChanges: this.formProvider.variationDetailMaterialChangesCtrl,
      otherChanges: this.formProvider.variationOtherChangesCtrl,
      nonMaterialChanges: this.formProvider.variationDetailNonMaterialChangesCtrl,
    },
    { validators: checkboxSectionsValidator(), updateOn: 'change' },
  );

  materialChanges = materialChanges;
  materialChangesKeys = Object.keys(materialChanges);
  otherChanges = otherChanges;
  otherChangesKeys = Object.keys(otherChanges);
  nonMaterialChanges = nonMaterialChanges;
  nonMaterialChangesKeys = Object.keys(nonMaterialChanges);

  constructor(
    @Inject(TASK_FORM_PROVIDER) private formProvider: VariationDetailsFormProvider,
    private store: RequestTaskStore,
    private pendingRequestService: PendingRequestService,
    private backLinkService: BackLinkService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit() {
    const variationDetails: EmpVariationCorsiaDetails = {
      reason: this.form.get('reason').value,
      changes: getChangesFormData(this.form.value),
    };
    const variationRegulatorLedReason = this.formProvider.getVariationRegulatorLedReasonFormValue();

    this.store.empCorsiaDelegate
      .saveEmp({ empVariationDetails: variationDetails }, 'in progress')
      .pipe(
        switchMap(() => this.store.pipe(requestTaskQuery.selectRequestTaskType)),
        first(),
        this.pendingRequestService.trackRequest(),
      )
      .subscribe((type) => {
        this.store.empCorsiaDelegate.setVariationDetails(variationDetails, variationRegulatorLedReason);
        this.formProvider.setFormValue(variationDetails, variationRegulatorLedReason);

        isReasonWizardRequired(type)
          ? this.router.navigate(['reason'], { relativeTo: this.route })
          : this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }
}
