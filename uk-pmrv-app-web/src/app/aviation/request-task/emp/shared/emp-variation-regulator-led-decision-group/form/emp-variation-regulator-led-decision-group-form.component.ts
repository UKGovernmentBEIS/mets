import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormArray } from '@angular/forms';

import { Observable } from 'rxjs';

import { EmpVariationRegulatorLedDecisionGroupFormProvider } from '@aviation/request-task/emp/shared/emp-variation-regulator-led-decision-group/emp-variation-regulator-led-decision-group-form.provider';
import { existingControlContainer } from '@shared/providers/control-container.factory';
import { SharedModule } from '@shared/shared.module';

import { requestTaskQuery, RequestTaskStore } from '../../../../store';

/* eslint-disable @angular-eslint/prefer-on-push-component-change-detection */
@Component({
  selector: 'app-emp-variation-regulator-led-decision-group-form',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './emp-variation-regulator-led-decision-group-form.component.html',
  viewProviders: [existingControlContainer],
})
export class EmpVariationRegulatorLedDecisionGroupFormComponent implements OnInit {
  @ViewChild('conditionalHeader') header: ElementRef;

  form = this.formProvider.form;
  isEditable$: Observable<boolean>;

  constructor(
    readonly store: RequestTaskStore,
    readonly formProvider: EmpVariationRegulatorLedDecisionGroupFormProvider,
  ) {}

  ngOnInit(): void {
    this.isEditable$ = this.store.pipe(requestTaskQuery.selectIsEditable);
  }

  addOtherVariationScheduleItem(): void {
    this.formProvider.addOtherVariationScheduleItem();
  }

  addVariationScheduleItem(): void {
    this.formProvider.addVariationScheduleItemsCtrl();
  }

  removeVariationScheduleItems(index: number) {
    this.formProvider.removeVariationScheduleItemsCtrl(index);
  }

  get variationScheduleItemsCtrl(): FormArray | null {
    return (this.form.get('variationScheduleItems') as FormArray) ?? null;
  }
}
