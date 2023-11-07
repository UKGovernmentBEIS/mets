import { CommonModule } from '@angular/common';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, startWith } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import {
  AircraftTypeDetailsWithIndex,
  AircraftTypeTableComponent,
  appendIndex,
} from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { SharedModule } from '@shared/shared.module';

import { AircraftTypeDetails } from 'pmrv-api';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionFactorsSummaryComponent } from '../emission-factors-summary/emission-factors-summary.component';
import { EmissionSourcesFormModel } from '../emission-sources-form.model';
import {
  addMultipleMethodsControl,
  addOtherFuelTypeExplanationControl,
  hasMoreThanOneMonitoringMethod,
  hasOtherFuelType,
} from '../emission-sources-form.provider';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-emission-sources-page',
  standalone: true,
  imports: [
    CommonModule,
    SharedModule,
    AircraftTypeTableComponent,
    EmissionFactorsSummaryComponent,
    ReturnToLinkComponent,
  ],
  templateUrl: './emission-sources-page.component.html',
})
export class EmissionSourcesPageComponent implements OnInit, OnDestroy {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);
  router = inject(Router);
  route = inject(ActivatedRoute);
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  fb = inject(FormBuilder);
  form = this.fb.group({
    aircraftTypes: this.emissionSourcesForm.controls.aircraftTypes,
  });
  counter$ = new BehaviorSubject(0);
  private backLinkService = inject(BackLinkService);
  aircratTypesControl = this.form.controls.aircraftTypes;
  aircraftTypesInUse$ = this.form.controls.aircraftTypes.valueChanges.pipe(
    startWith(this.form.controls.aircraftTypes.value),
    map(
      (aircrafTypes: AircraftTypeDetails[]) =>
        aircrafTypes
          .map(appendIndex)
          .filter(({ isCurrentlyUsed }) => isCurrentlyUsed) as AircraftTypeDetailsWithIndex[],
    ),
  );
  aircraftTypesToBeUsed$ = this.form.controls.aircraftTypes.valueChanges.pipe(
    startWith(this.form.controls.aircraftTypes.value),
    map(
      (aircrafTypes: AircraftTypeDetails[]) =>
        aircrafTypes
          .map(appendIndex)
          .filter(({ isCurrentlyUsed }) => !isCurrentlyUsed) as AircraftTypeDetailsWithIndex[],
    ),
  );
  isFUMM = isFUMM(this.store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts);

  ngOnInit(): void {
    this.backLinkService.show('..');
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  onSubmit() {
    this.form.controls.aircraftTypes.markAsTouched();
    this.form.controls.aircraftTypes.updateValueAndValidity();
    if (this.form.invalid) {
      return;
    }
    const value = this.emissionSourcesForm.getRawValue();
    if (hasOtherFuelType(this.form)) {
      addOtherFuelTypeExplanationControl(this.emissionSourcesForm, this.fb);
      this.router.navigate(['other-fuel'], { relativeTo: this.route });
      return;
    } else if (isFUMM(this.store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts)) {
      if (hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
        addMultipleMethodsControl(this.emissionSourcesForm, this.fb);
        this.router.navigate(['./multiple-methods'], { relativeTo: this.route, queryParamsHandling: 'merge' });
      } else {
        this.router.navigate(['./monitoring-methodology'], { relativeTo: this.route, queryParamsHandling: 'merge' });
      }
    } else {
      this.store.empUkEtsDelegate
        .saveEmp({ emissionSources: value }, 'in progress')
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => {
          this.router.navigate(['./summary'], {
            relativeTo: this.route,
            queryParamsHandling: 'merge',
          });
        });
    }
  }

  addAircraftType(isCurrentlyUsed = true) {
    this.router.navigate(['aircraft-type', 'add'], {
      queryParams: { isCurrentlyUsed: isCurrentlyUsed ? 1 : 0, change: 'true' },
      relativeTo: this.route,
    });
  }
}
