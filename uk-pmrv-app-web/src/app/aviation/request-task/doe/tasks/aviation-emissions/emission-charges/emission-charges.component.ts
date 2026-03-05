import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

@Component({
  selector: 'app-doe-emissions-charges',
  templateUrl: './emission-charges.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EmissionsChargesComponent extends BaseDoeCorsiaEmissionsComponent {
  form = this.formProvider.chargeOperatorCtrl;

  onSubmit() {
    this.saveDoeAndNavigate(
      this.formProvider.getFormValue(),
      'in progress',
      this.form.value.chargeOperator ? '../emission-charges-calculation' : '../summary',
    );
  }
}
