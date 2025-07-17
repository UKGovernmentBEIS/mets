import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { AerService } from '@tasks/aer/core/aer.service';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

@Component({
  selector: 'app-combustion-source-delete',
  template: `
    <ng-container *ngIf="combustionSource$ | async as combustionSource">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap">‘{{ combustionSource }}’?</span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button type="button" (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
        <a govukLink routerLink="." (click)="this.location.back()">Cancel</a>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CombustionSourceDeleteComponent implements PendingRequest {
  combustionSource$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('combustionSource')));

  constructor(
    readonly pendingRequest: PendingRequestService,
    readonly location: Location,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onDelete(): void {
    this.aerService
      .getPayload()
      .pipe(
        first(),
        map(
          (payload) =>
            (payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport.opinionStatement,
        ),
        withLatestFrom(this.combustionSource$),
        switchMap(([opinionStatementData, combustionSource]) => {
          opinionStatementData.combustionSources = opinionStatementData.combustionSources.filter(
            (item) => item !== combustionSource,
          );
          return this.aerService.postVerificationTaskSave(
            {
              opinionStatement: {
                ...opinionStatementData,
              },
            },
            false,
            'opinionStatement',
          );
        }),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
