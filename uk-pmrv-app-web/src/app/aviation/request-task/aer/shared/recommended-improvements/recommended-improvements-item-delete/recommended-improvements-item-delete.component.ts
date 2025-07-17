import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerRecommendedImprovements } from 'pmrv-api';

@Component({
  selector: 'app-recommended-improvements-item-delete',
  template: `
    <div class="govuk-!-width-two-thirds">
      <app-page-heading size="l">Are you sure you want to delete ‘{{ reference$ | async }}’?</app-page-heading>
      <p class="govuk-body">Any reference to this item will be removed from your report.</p>
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
export class RecommendedImprovementsItemDeleteComponent {
  reference$ = this.route.paramMap.pipe(map((paramMap) => 'D' + (+paramMap.get('index') + 1)));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete(): void {
    let improvementsValue: AviationAerRecommendedImprovements;
    combineLatest([this.route.paramMap, this.store.pipe(recommendedImprovementsQuery.selectRecommendedImprovements)])
      .pipe(
        first(),
        switchMap(([paramMap, recommendedImprovements]) => {
          improvementsValue = {
            ...recommendedImprovements,
            recommendedImprovements: recommendedImprovements.recommendedImprovements
              .filter((item, idx) => idx !== +paramMap.get('index'))
              .map((item, idx) => ({
                reference: `D${idx + 1}`,
                explanation: item.explanation,
              })),
          };

          return this.store.aerVerifyDelegate.saveAerVerify(
            {
              recommendedImprovements: improvementsValue,
            },
            'in progress',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route, queryParams: { change: true } }));
  }
}
