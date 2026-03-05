import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, SourceStream } from 'pmrv-api';

import { PendingRequestService } from '../../../../../../core/guards/pending-request.service';
import { AerService } from '../../../../core/aer.service';

@Component({
  selector: 'app-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <ng-container *ngIf="sourceStream$ | async as sourceStream">
        <span class="nowrap">
          ‘{{ sourceStream?.sourceStreamInfo?.reference }}
          {{ sourceStream?.sourceStreamInfo?.description | sourceStreamDescription }}
          <ng-container *ngIf="sourceStream?.isDataGap">- Data gap</ng-container>
          ’?
        </span>
      </ng-container>
    </app-page-heading>

    <p class="govuk-body">All the information within this source stream will be deleted.</p>

    <div class="govuk-button-group">
      <button type="button" (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
      <a govukLink routerLink="." (click)="location.back()">Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  taskKey = this.route.snapshot.data.taskKey;

  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  sourceStream$: Observable<{ sourceStreamInfo: SourceStream; isDataGap: boolean }> = combineLatest([
    this.payload$,
    this.index$,
  ]).pipe(
    map(([payload, index]) => {
      const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
      const approach = monitoringApproachEmissions[this.taskKey] as any;

      const sourceStreamEmission = approach?.sourceStreamEmissions?.[index] as any;
      const sourceStreams = payload.aer.sourceStreams;

      const sourceStreamInfo = sourceStreams.find(
        (sourceStream) => sourceStream.id === sourceStreamEmission?.sourceStream,
      );
      const isDataGap = sourceStreamEmission?.parameterMonitoringTierDiffReason?.type === 'DATA_GAP';

      return { sourceStreamInfo, isDataGap };
    }),
  );

  constructor(
    readonly location: Location,
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onDelete() {
    combineLatest([this.payload$, this.index$])
      .pipe(
        first(),
        switchMap(([payload, index]) =>
          this.aerService.postTaskSave(
            this.buildData(payload, index),
            undefined,
            this.buildStatus(payload, index),
            this.taskKey,
          ),
        ),

        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.navigateNext());
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const approach = monitoringApproachEmissions[this.taskKey] as any;

    const sourceStreamEmissions = approach?.sourceStreamEmissions?.filter((_, idx) => idx !== index);

    const data = {
      monitoringApproachEmissions: {
        ...monitoringApproachEmissions,
        [this.taskKey]: {
          ...approach,
          type: this.taskKey,
          sourceStreamEmissions: sourceStreamEmissions,
        },
      },
    };

    return data;
  }
  private buildStatus(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const approach = payload.aer.monitoringApproachEmissions[this.taskKey] as any;

    return approach?.sourceStreamEmissions?.length === payload.aerSectionsCompleted[this.taskKey]?.length
      ? payload.aerSectionsCompleted[this.taskKey]?.filter((_, idx) => index !== idx)
      : Array(approach.sourceStreamEmissions.length - 1).fill(false);
  }

  private navigateNext() {
    const nextStep = '../..';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
