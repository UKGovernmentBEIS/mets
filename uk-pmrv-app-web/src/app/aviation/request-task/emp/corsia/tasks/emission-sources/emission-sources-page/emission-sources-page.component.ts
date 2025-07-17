import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, map, startWith } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import {
  AircraftTypeTableComponent,
  appendIndex,
} from '@aviation/shared/components/emp/emission-sources/aircraft-type/table/aircraft-type-table.component';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionFactorsSummaryComponent } from '../emission-factors-summary/emission-factors-summary.component';
import { EmissionSourcesFormModelCorsia } from '../emission-sources-form.model';
import { addMultipleMethodsControl, hasMoreThanOneMonitoringMethod } from '../emission-sources-form.provider';

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
export class EmissionSourcesPageComponent {
  private store = inject(RequestTaskStore);
  private pendingRequestService = inject(PendingRequestService);

  router = inject(Router);
  route = inject(ActivatedRoute);
  emissionSourcesForm = inject<FormGroup<EmissionSourcesFormModelCorsia>>(TASK_FORM_PROVIDER);
  fb = inject(FormBuilder);

  form = this.fb.group({
    aircraftTypes: this.emissionSourcesForm.controls.aircraftTypes,
  });

  counter$ = new BehaviorSubject(0);
  aircratTypesControl = this.form.controls.aircraftTypes;

  aircraftTypes$ = this.form.controls.aircraftTypes.valueChanges.pipe(
    startWith(this.form.controls.aircraftTypes.value),
    map((aircrafTypes) => aircrafTypes.map(appendIndex as any)),
  );

  isFUMM = isFUMM(this.store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts);

  onSubmit() {
    this.form.controls.aircraftTypes.markAsTouched();
    this.form.controls.aircraftTypes.updateValueAndValidity();

    if (this.form.invalid) return;

    const value = this.emissionSourcesForm.getRawValue();

    if (this.isFUMM && hasMoreThanOneMonitoringMethod(this.emissionSourcesForm)) {
      addMultipleMethodsControl(this.emissionSourcesForm, this.fb);

      this.router.navigate(['./multiple-methods'], { relativeTo: this.route, queryParamsHandling: 'merge' });
    } else {
      this.store.empCorsiaDelegate
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

  addAircraftType() {
    this.router.navigate(['aircraft-type', 'add'], {
      queryParams: { change: 'true' },
      relativeTo: this.route,
    });
  }
}
