import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { AviationAerComplianceMonitoringReportingRules, AviationAerUkEtsVerificationReport } from 'pmrv-api';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { ComplianceMonitoringFormProvider } from './compliance-monitoring-form.provider';

interface ViewModel {
  isEditable: boolean;
  verificationReportData: AviationAerUkEtsVerificationReport;
}

@Component({
  selector: 'app-compliance-monitoring-page',
  templateUrl: './compliance-monitoring-page.component.html',
  standalone: true,
  imports: [SharedModule, ReturnToLinkComponent],
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export default class ComplianceMonitoringPageComponent {
  protected form = this.formProvider.form;
  protected verificationReportData: AviationAerUkEtsVerificationReport;

  constructor(
    @Inject(TASK_FORM_PROVIDER) readonly formProvider: ComplianceMonitoringFormProvider,
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
    const value = { ...this.form.value } as AviationAerComplianceMonitoringReportingRules;
    (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate)
      .saveAerVerify({ complianceMonitoringReportingRules: { ...value } }, 'in progress')
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        (this.store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setComplianceMonitoring({
          ...value,
        });
        this.router.navigate(['summary'], { relativeTo: this.route, queryParams: { change: true } });
      });
  }
}
