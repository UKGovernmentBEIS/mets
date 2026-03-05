import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { ALRGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-preliminary-allocation-delete',
  template: `
    <app-page-heading size="xl">Are you sure you want to delete this item?</app-page-heading>

    <p class="govuk-body">Any reference to this item will be removed from your application.</p>

    <div class="govuk-button-group" *ngIf="editable$ | async">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  standalone: true,
  imports: [SharedModule, TaskSharedModule, AlrTaskSharedModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrPreliminaryAllocationDeleteComponent {
  index = this.route.snapshot.paramMap.get('index');
  editable$ = this.alrService.isEditable$;

  constructor(
    readonly alrService: AlrService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.alrService.authorityPayload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.alrService.postAlrAuthority(
            {
              authorityResponse: {
                ...payload.authorityReviewOutcome.authorityResponse,
                preliminaryAllocations: (
                  payload.authorityReviewOutcome.authorityResponse as ALRGrantAuthorityResponse
                )?.preliminaryAllocations
                  ?.filter((_, i) => i !== Number(this.index))
                  .map((element, index) => {
                    return { ...element, allocationId: index + '' };
                  }),
              } as ALRGrantAuthorityResponse,
            },
            'authorityResponse',
            false,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
