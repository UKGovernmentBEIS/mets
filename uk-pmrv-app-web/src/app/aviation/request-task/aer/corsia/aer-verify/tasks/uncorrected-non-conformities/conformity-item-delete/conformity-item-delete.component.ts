import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

@Component({
  selector: 'app-conformity-item-delete',
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
  imports: [SharedModule, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConformityItemDeleteComponent {
  reference$ = this.route.paramMap.pipe(map((paramMap) => 'B' + (+paramMap.get('index') + 1)));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  delete(): void {
    combineLatest([this.route.paramMap, this.store.pipe(aerVerifyCorsiaQuery.selectUncorrectedNonConformities)])
      .pipe(
        first(),
        switchMap(([paramMap, uncorrectedNonConformities]) => {
          const conformitiesValue = {
            ...uncorrectedNonConformities,
            uncorrectedNonConformities: uncorrectedNonConformities.uncorrectedNonConformities
              .filter((item, idx) => idx !== +paramMap.get('index'))
              .map((item, idx) => ({
                reference: `B${idx + 1}`,
                explanation: item.explanation,
                materialEffect: item.materialEffect,
              })),
          };

          return (this.store.aerVerifyDelegate as AerVerifyCorsiaStoreDelegate).saveAerVerify(
            {
              uncorrectedNonConformities: conformitiesValue,
            },
            'in progress',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route, queryParams: { change: true } }));
  }
}
