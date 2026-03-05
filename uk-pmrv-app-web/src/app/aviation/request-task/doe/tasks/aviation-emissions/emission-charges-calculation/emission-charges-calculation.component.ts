import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

@Component({
  selector: 'app-doe-emission-charges-calculation',
  templateUrl: './emission-charges-calculation.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EmissionsChargesCalculationComponent extends BaseDoeCorsiaEmissionsComponent implements OnInit {
  form = this.formProvider.feeCtrl;

  totalOperatorFee = signal(0);

  ngOnInit(): void {
    if (this.form?.valid) {
      this.totalOperatorFee.set(+this.form.value.totalBillableHours * +this.form.value.hourlyRate);
    }

    this.form?.valueChanges.subscribe((r) => {
      this.totalOperatorFee.set(+r.totalBillableHours * +r.hourlyRate);
    });
  }

  onSubmit() {
    this.saveDoeAndNavigate(this.formProvider.getFormValue(), 'in progress', '../summary');
  }
}
