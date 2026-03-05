import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { AerService } from '../../../core/aer.service';
import { buildTaskData } from '../measurement';
import { getCompletionStatus } from '../measurement-status';

@Component({
  selector: 'app-transferred-details',
  templateUrl: './transferred-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferredDetailsComponent {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  payload$ = this.aerService.getPayload();

  taskKey = this.route.snapshot.data.taskKey;
  isEditable$ = this.aerService.isEditable$;

  emissionPointEmission$ = combineLatest([this.payload$, this.index$]).pipe(
    map(([payload, index]) => {
      const res = (payload.aer.monitoringApproachEmissions[this.taskKey] as any)?.emissionPointEmissions?.[index];
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
              getCompletionStatus(this.taskKey, payload, index, false),
              this.taskKey,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.navigateNext());
    }
  }

  private buildData(payload: AerApplicationSubmitRequestTaskPayload, index: number) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const measurement = monitoringApproachEmissions[this.taskKey] as any;

    const emissionPointEmission = measurement.emissionPointEmissions?.[index];

    const installationDetailsType = this.form.get('installationDetailsType').value;

    const transfer = {
      transfer: {
        ...emissionPointEmission?.transfer,
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

    const emissionPointEmissions = measurement.emissionPointEmissions.map((item, idx) =>
      idx === index
        ? {
            ...item,
            ...transfer,
          }
        : item,
    );

    const data = buildTaskData(this.taskKey, payload, emissionPointEmissions);

    return data;
  }

  private navigateNext() {
    this.router.navigate(['../date-range'], { relativeTo: this.route });
  }
}
