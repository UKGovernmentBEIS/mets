import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationAerSmallEmittersMonitoringApproachFormModel } from '../monitoring-approach-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-monitoring-approach-number-flights-form',
  templateUrl: './monitoring-approach-number-flights-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule],
  viewProviders: [existingControlContainer],
})
export class MonitoringApproachNumberFlightsFormComponent implements OnInit {
  @Input() form: FormGroup<AviationAerSmallEmittersMonitoringApproachFormModel>;
  totalNumberOfFlights = 0;

  ngOnInit(): void {
    if (this.form?.valid) {
      this.totalNumberOfFlights =
        +this.form.value.numOfFlightsJanApr + +this.form.value.numOfFlightsMayAug + +this.form.value.numOfFlightsSepDec;
    }

    this.form?.valueChanges.subscribe((r) => {
      this.totalNumberOfFlights = +r.numOfFlightsJanApr + +r.numOfFlightsMayAug + +r.numOfFlightsSepDec;
    });
  }
}
