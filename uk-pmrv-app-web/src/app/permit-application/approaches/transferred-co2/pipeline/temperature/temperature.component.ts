import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  OnInit,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { UntypedFormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, switchMap, takeUntil, tap } from 'rxjs';

import { ConfigStore } from '@core/config/config.store';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { WizardStepComponent } from '@shared/wizard/wizard-step.component';

import { typeOptions } from '../../../../measurement-devices/measurement-device-details/measurement-device-details-form.provider';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { createAnotherMeasurementDevice, temperatureFormProvider } from './temperature-form.provider';

@Component({
  selector: 'app-temperature',
  templateUrl: './temperature.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [temperatureFormProvider, DestroySubject],
})
export class TemperatureComponent implements OnInit, AfterViewInit {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;
  measurementTypes = typeOptions;

  private measurementDevicesLength = this.measurementDevices.length;

  private readonly configFeatures$ = this.configStore.asObservable().pipe(map((state) => state.features));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly pendingRequest: PendingRequestService,
    private readonly configStore: ConfigStore,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get measurementDevices(): UntypedFormArray {
    return this.form.get('measurementDevices') as UntypedFormArray;
  }

  getDeviceType(index: number): string {
    return this.measurementDevices.at(index).get('type').value;
  }

  ngOnInit(): void {
    this.configFeatures$
      .pipe(
        takeUntil(this.destroy$),
        tap((features) => {
          const wastePermitEnabled = features.wastePermitEnabled;

          this.measurementTypes = typeOptions;

          if (!wastePermitEnabled) {
            this.measurementTypes = this.measurementTypes.filter((option) => option !== 'CRANE_WEIGHT');
          }
        }),
      )
      .subscribe();
  }

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.measurementDevicesLength) {
        buttons.last.nativeElement.focus();
      }
      this.measurementDevicesLength = buttons.length;
    });
  }

  onContineu(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postTask(data.taskKey, this.form.value, false, data.statusKey)),
        this.pendingRequest.trackRequest(),
        switchMap(() => this.store),
        first(),
      )
      .subscribe(() => this.router.navigate(['../transfer-co2'], { relativeTo: this.route }));
  }

  addMeasurementDevice(): void {
    this.measurementDevices.push(createAnotherMeasurementDevice());

    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.measurementDevices.at(this.measurementDevices.length - 1).markAllAsTouched());
  }
}
