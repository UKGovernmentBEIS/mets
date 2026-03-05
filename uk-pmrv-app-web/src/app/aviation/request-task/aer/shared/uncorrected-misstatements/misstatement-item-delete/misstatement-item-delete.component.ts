import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

@Component({
  selector: 'app-misstatement-item-delete',
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
export class MisstatementItemDeleteComponent {
  reference$ = this.route.paramMap.pipe(map((paramMap) => 'A' + (+paramMap.get('index') + 1)));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete(): void {
    combineLatest([this.route.paramMap, this.store.pipe(uncorrectedMisstatementsQuery.selectUncorrectedMisstatements)])
      .pipe(
        first(),
        switchMap(([paramMap, uncorrectedMisstatements]) => {
          const misstatementsValue = {
            ...uncorrectedMisstatements,
            uncorrectedMisstatements: uncorrectedMisstatements.uncorrectedMisstatements
              .filter((item, idx) => idx !== +paramMap.get('index'))
              .map((item, idx) => ({
                reference: `A${idx + 1}`,
                explanation: item.explanation,
                materialEffect: item.materialEffect,
              })),
          };

          return this.store.aerVerifyDelegate.saveAerVerify(
            {
              uncorrectedMisstatements: misstatementsValue,
            },
            'in progress',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route, queryParams: { change: true } }));
  }
}
