import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { getCompletionStatus } from '../../../shared/components/submit/emissions-status';
import { buildTaskData } from '../calculation-emissions';

@Component({
  selector: 'app-transferred-details',
  templateUrl: './transferred-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredDetailsComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  isEditable$ = this.aerService.isEditable$;

  sourceStreamEmission$: Observable<CalculationSourceStreamEmission> = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
        ?.sourceStreamEmissions?.[index];
      return res;
    }),
  );

  form: UntypedFormGroup;

  constructor(
    readonly aerService: AerService,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue(form: UntypedFormGroup): void {
    this.form = form;

    if (!form.dirty) {
      this.navigateNext();
    } else {
      combineLatest([this.payload$, this.index$])
        .pipe(
          first(),
          switchMap(([payload, index]) =>
            this.aerService.postTaskSave(
              this.buildData(payload, index),
              undefined,
              getCompletionStatus('CALCULATION_CO2', payload, index, false),
              'CALCULATION_CO2',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const sourceStreamEmission = calculation.sourceStreamEmissions?.[index];

    const installationDetailsType = this.form.get('installationDetailsType').value;

    const transfer = {
      transfer: {
        ...sourceStreamEmission?.transfer,
        installationDetailsType,
        ...(installationDetailsType === 'INSTALLATION_EMITTER'
          ? {
              installationEmitter: {
                emitterId: this.form.controls.emitterId.value,
                email: this.form.controls.email.value,
              },
            }
          : {
              installationEmitter: null,
            }),
        ...(installationDetailsType === 'INSTALLATION_DETAILS'
          ? {
              installationDetails: {
                city: this.form.controls.city.value,
                email: this.form.controls.email2.value,
                installationName: this.form.controls.installationName.value,
                line1: this.form.controls.line1.value,
                line2: this.form.controls?.line2.value,
                postcode: this.form.controls.postcode.value,
              },
            }
          : {
              installationDetails: null,
            }),
      },
    };

    const sourceStreamEmissions = calculation.sourceStreamEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...transfer,
          }
        : item,
    );

    const data = buildTaskData(payload, sourceStreamEmissions);

    return data;
  }

  private navigateNext() {
    this.router.navigate(['../date-range'], { relativeTo: this.route });
  }
}
