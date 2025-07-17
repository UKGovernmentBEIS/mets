import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable, tap } from 'rxjs';

import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { OverallDecisionTypePipe } from '@shared/pipes/overall-decision-type.pipe';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerVerificationDecision, AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

import { OverallDecisionFormProvider } from '../overall-decision-form.provider';

interface ViewModel {
  pageHeader: string;
  isEditable: boolean;
  overallDecision: AviationAerVerificationDecision;
  isCorsia: boolean;
}

@Component({
  selector: 'app-overall-decision',
  templateUrl: './overall-decision.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent, OverallDecisionTypePipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OverallDecisionComponent {
  private overallDecision: AviationAerVerificationDecision;
  vm$: Observable<ViewModel> = combineLatest([
    this.store.pipe(requestTaskQuery.selectRequestInfo),
    this.store.pipe(requestTaskQuery.selectIsEditable),
    this.store.pipe(overallDecisionQuery.selectOverallDecision),
  ]).pipe(
    map(([requestInfo, isEditable, overallDecision]) => ({
      pageHeader: 'What is your assessment of this report?',
      isEditable,
      overallDecision,
      isCorsia: CorsiaRequestTypes.includes(requestInfo.type),
    })),
    tap((data) => {
      this.overallDecision = data.overallDecision;
    }),
  );
  form = new FormGroup({
    type: this.formProvider.typeCtrl,
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) protected readonly formProvider: OverallDecisionFormProvider,
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.nextUrl();
    } else {
      const value = { ...this.form.value };
      this.store.aerVerifyDelegate
        .saveAerVerify({ overallDecision: { ...value } }, 'in progress')
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.nextUrl());
    }
  }

  private nextUrl() {
    switch (this.form.get('type').value) {
      case 'VERIFIED_AS_SATISFACTORY':
        return this.router.navigate(['summary'], { relativeTo: this.route, queryParams: { change: true } });

      case 'VERIFIED_AS_SATISFACTORY_WITH_COMMENTS':
        if ((this.overallDecision as AviationAerVerifiedSatisfactoryWithCommentsDecision)?.reasons?.length > 0) {
          return this.router.navigate(['reason-list'], { relativeTo: this.route, queryParams: { change: true } });
        } else {
          return this.router.navigate(['reason-list/0'], { relativeTo: this.route, queryParams: { change: true } });
        }

      case 'NOT_VERIFIED':
        return this.router.navigate(['not-verified'], { relativeTo: this.route, queryParams: { change: true } });
    }
  }
}
