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
  selector: 'app-material-misstatement',
  templateUrl: './material-misstatement.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MaterialMisstatementComponent {
  protected verificationReportData: AviationAerUkEtsVerificationReport;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectVerificationReport),
  ]).pipe(
    map(([isEditable, verificationReport]) => ({
      pageHeader: 'Did the method lead to a material misstatement?',
      isEditable,
      verificationReport,
    })),
    tap((data) => {
      this.verificationReportData = data.verificationReport;
    }),
  );
  form = new FormGroup({
    materialMisstatementExist: this.formProvider.materialMisstatementExistCtrl,
    materialMisstatementDetails: this.formProvider.materialMisstatementDetailsCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: DataGapsMethodologiesFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (this.form.invalid) return;

    const parentFormValue = this.formProvider.getFormValue();
    const value = {
      ...this.form.value,
      materialMisstatementDetails: this.form.value.materialMisstatementExist
        ? this.form.value.materialMisstatementDetails
        : null,
    };
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
    return this.router.navigate(['../summary'], { relativeTo: this.route });
  }
}
