import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload, EmissionPoint } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';

@Component({
  selector: 'app-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <ng-container *ngIf="emissionPoint$ | async as emissionPoint">
        <span class="nowrap">
          ‘{{ emissionPoint?.emissionPointInfo?.reference }}
          {{ emissionPoint?.emissionPointInfo?.description }}
          <ng-container *ngIf="emissionPoint?.isDataGap">- Data gap</ng-container>
          ’?
        </span>
      </ng-container>
    </app-page-heading>

    <p class="govuk-body">All the information within this emission point will be deleted.</p>

    <div class="govuk-button-group">
      <button type="button" (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
      <a govukLink routerLink="." (click)="location.back()">Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  emissionPoint$: Observable<{ emissionPointInfo: EmissionPoint; isDataGap: boolean }> = combineLatest([
    this.payload$,
    this.index$,
  ]).pipe(
    map(([payload, index]) => {
      const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
      const measurement = monitoringApproachEmissions[this.taskKey] as any;

      const emissionPointEmission = measurement?.emissionPointEmissions?.[index];
      const emissionPoints = payload.aer.emissionPoints;

      const emissionPointInfo = emissionPoints.find(
        (emissionPoint) => emissionPoint.id === emissionPointEmission?.emissionPoint,
      );
      const isDataGap = emissionPointEmission?.parameterMonitoringTierDiffReason?.type === 'DATA_GAP';

      return { emissionPointInfo, isDataGap };
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
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmissions = measurement?.emissionPointEmissions?.filter((_, idx) => idx !== index);

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }
  private buildStatus(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const measurement = payload.aer.monitoringApproachEmissions[this.taskKey] as any;
    return measurement?.emissionPointEmissions?.length === payload.aerSectionsCompleted[this.taskKey]?.length
      ? payload.aerSectionsCompleted[this.taskKey]?.filter((_, idx) => index !== idx)
      : Array(measurement.emissionPointEmissions.length - 1).fill(false);
  }

  private navigateNext() {
    const nextStep = '../..';

    this.router.navigate([`${nextStep}`], { relativeTo: this.route });
  }
}
