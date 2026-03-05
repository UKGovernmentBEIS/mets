import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, takeUntil, tap } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';

import { MeasurementDeviceOrMethod } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { measurementDevicesAddFormFactory, typeOptions } from './measurement-device-details-form.provider';

@Component({
  selector: 'app-measurement-device-details',
  templateUrl: './measurement-device-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesAddFormFactory, DestroySubject],
})
export class MeasurementDeviceDetailsComponent implements OnInit {
  requestTaskType$ = this.store.pipe(map((response) => reviewRequestTaskTypes.includes(response.requestTaskType)));
  isEditing$ = this.store
    .getTask('measurementDevicesOrMethods')
    .pipe(
      map((measurementDevicesOrMethods) =>
        measurementDevicesOrMethods.some((item) => item.id === this.form.get('id').value),
      ),
    );
  typeOptions = typeOptions;
  private readonly configFeatures$ = this.configStore.asObservable().pipe(map((state) => state.features));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly configStore: ConfigStore,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.configFeatures$
      .pipe(
        takeUntil(this.destroy$),
        tap((features) => {
          const wastePermitEnabled = features.wastePermitEnabled;

          this.typeOptions = typeOptions;

          if (!wastePermitEnabled) {
            this.typeOptions = this.typeOptions.filter((option) => option !== 'CRANE_WEIGHT');
          }
        }),
      )
      .subscribe();
  }

  get type(): string {
    return this.form.get('type').value;
  }

  onSubmit(): void {
    this.store
      .findTask<MeasurementDeviceOrMethod[]>('measurementDevicesOrMethods')
      .pipe(
        first(),
        switchMap((devices) =>
          this.store.postTask(
            'measurementDevicesOrMethods',
            devices.some((item) => item.id === this.form.value.id)
              ? devices.map((item) => (item.id === this.form.value.id ? this.form.value : item))
              : [...devices, this.form.value],
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
