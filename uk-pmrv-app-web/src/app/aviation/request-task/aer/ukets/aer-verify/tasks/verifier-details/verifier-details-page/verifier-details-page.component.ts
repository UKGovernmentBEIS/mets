import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { VerifierDetailsFormProvider } from '../verifier-details-form.provider';
import AerVerifierDetailsGroupFormComponent from '../verifier-details-group-form/verifier-details-group-form.component';

interface ViewModel {
  isEditable: boolean;
  verificationReportData: AviationAerUkEtsVerificationReport;
}

@Component({
  selector: 'app-aer-verifier-details-page',
  templateUrl: './verifier-details-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent, AerVerifierDetailsGroupFormComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class AerVerifierDetailsPageComponent {
  protected form = this.formProvider.form;
  protected verificationReportData: AviationAerUkEtsVerificationReport;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: VerifierDetailsFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(aerVerifyQuery.selectVerificationReport),
  ]).pipe(
    map(([isEditable, verificationReportData]) => {
      return {
        isEditable,
        verificationReportData,
      };
    }),
    tap((data) => {
      this.verificationReportData = data.verificationReportData;
    }),
  );

  onSubmit() {
    if (this.form.invalid) return;

    const verificationReport: AviationAerUkEtsVerificationReport = {
      ...this.verificationReportData,
      verifierContact: {
        name: this.form.value.name,
        email: this.form.value.email,
        phoneNumber: this.form.value.phoneNumber,
      },
      verificationTeamDetails: {
        leadEtsAuditor: this.form.value.leadEtsAuditor,
        etsAuditors: this.form.value.etsAuditors,
        etsTechnicalExperts: this.form.value.etsTechnicalExperts,
        independentReviewer: this.form.value.independentReviewer,
        technicalExperts: this.form.value.technicalExperts,
        authorisedSignatoryName: this.form.value.authorisedSignatoryName,
      },
    };

    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ verificationReport }, 'in progress')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setVerificationReport(verificationReport);
        this.router.navigate(['summary'], { relativeTo: this.route });
      });
  }
}
