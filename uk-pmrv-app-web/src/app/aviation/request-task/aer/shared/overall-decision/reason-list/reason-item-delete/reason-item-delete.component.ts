import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerVerifiedSatisfactoryWithCommentsDecision } from 'pmrv-api';

@Component({
  selector: 'app-reason-item-delete',
  template: `
    <div class="govuk-!-width-two-thirds">
      <app-page-heading size="l"> Are you sure you want to delete this item? </app-page-heading>
      <p class="govuk-body">Any reference to this item will be removed from your application.</p>
      <div class="govuk-button-group">
        <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      </div>
      <app-return-to-link></app-return-to-link>
    </div>
  `,
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonItemDeleteComponent {
  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete(): void {
    let decisionValue: AviationAerVerifiedSatisfactoryWithCommentsDecision;
    combineLatest([this.route.paramMap, this.store.pipe(overallDecisionQuery.selectOverallDecision)])
      .pipe(
        first(),
        switchMap(([paramMap, overallDecision]) => {
          decisionValue = {
            ...overallDecision,
            reasons: (overallDecision as AviationAerVerifiedSatisfactoryWithCommentsDecision).reasons.filter(
              (item, idx) => idx !== +paramMap.get('index'),
            ),
          };

          return this.store.aerVerifyDelegate.saveAerVerify(
            {
              overallDecision: decisionValue,
            },
            'in progress',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route, queryParams: { change: true } }));
  }
}
