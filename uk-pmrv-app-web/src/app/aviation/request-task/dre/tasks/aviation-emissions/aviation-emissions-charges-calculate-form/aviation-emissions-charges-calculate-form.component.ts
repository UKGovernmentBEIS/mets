import { NgFor, NgIf } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationDreFeeDetailsFormModel } from '../aviation-emissions-form.provider';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-aviation-emissions-charges-calculate-form',
  templateUrl: './aviation-emissions-charges-calculate-form.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor],
  viewProviders: [existingControlContainer],
})
export class AviationEmissionsChargesCalculateFormComponent implements OnInit {
  @Input() form: FormGroup<AviationDreFeeDetailsFormModel>;
  totalOperatorFee = 0;

  ngOnInit(): void {
    if (this.form?.valid) {
      this.totalOperatorFee = +this.form.value.totalBillableHours * +this.form.value.hourlyRate;
    }

    this.form?.valueChanges.subscribe((r) => {
      this.totalOperatorFee = +r.totalBillableHours * +r.hourlyRate;
    });
  }
}
