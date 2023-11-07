import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DoalService } from '@tasks/doal/core/doal.service';

import { DoalGrantAuthorityResponse } from 'pmrv-api';

@Component({
  selector: 'app-preliminary-allocation-delete',
  template: `
    <app-page-heading size="xl"> Are you sure you want to delete this item? </app-page-heading>

    <p class="govuk-body">Any reference to this item will be removed from your application.</p>

    <div class="govuk-button-group" *ngIf="editable$ | async">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink="../.." govukLink>Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  index = this.route.snapshot.paramMap.get('index');
  editable$ = this.doalService.isEditable$;

  constructor(
    readonly doalService: DoalService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  delete(): void {
    this.doalService.authorityPayload$
      .pipe(
        first(),
        switchMap((payload) =>
          this.doalService.saveDoalAuthority(
            {
              authorityResponse: {
                ...payload.doalAuthority.authorityResponse,
                preliminaryAllocations: (
                  payload.doalAuthority.authorityResponse as DoalGrantAuthorityResponse
                )?.preliminaryAllocations?.filter((_, i) => i !== Number(this.index)),
              } as DoalGrantAuthorityResponse,
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
