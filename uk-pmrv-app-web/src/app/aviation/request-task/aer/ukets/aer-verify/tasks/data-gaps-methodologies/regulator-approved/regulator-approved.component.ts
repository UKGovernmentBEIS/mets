import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerDataGapsMethodologies, AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { aerVerifyQuery } from '../../../aer-verify.selector';
import { DataGapsMethodologiesFormProvider } from '../data-gaps-methodologies-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  verificationReport: AviationAerUkEtsVerificationReport;
}

@Component({
  selector: 'app-regulator-approved',
  templateUrl: './regulator-approved.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatorApprovedComponent {
  protected verificationReportData: AviationAerUkEtsVerificationReport;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectVerificationReport),
  ]).pipe(
    map(([isEditable, verificationReport]) => ({
      pageHeader: 'Has the data gap method already been approved by the regulator?',
      isEditable,
      verificationReport,
    })),
    tap((data) => {
      this.verificationReportData = data.verificationReport;
    }),
  );
  form = new FormGroup({
    methodApproved: this.formProvider.methodApprovedCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: DataGapsMethodologiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private readonly store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.invalid) return;

    const parentFormValue = this.formProvider.getFormValue();
    const value = { ...this.form.value };
    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ dataGapsMethodologies: { ...parentFormValue, ...value } }, 'in progress')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.formProvider.setFormValue({
          ...parentFormValue,
          ...value,
        } as AviationAerDataGapsMethodologies);
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setDataGapsMethodologies({
          ...parentFormValue,
          ...value,
        } as AviationAerDataGapsMethodologies);
        this.nextUrl();
      });
  }

  private nextUrl() {
    return this.form.get('methodApproved').value
      ? this.router.navigate(['../summary'], { relativeTo: this.route })
      : this.router.navigate(['../method-conservative'], { relativeTo: this.route });
  }
}
