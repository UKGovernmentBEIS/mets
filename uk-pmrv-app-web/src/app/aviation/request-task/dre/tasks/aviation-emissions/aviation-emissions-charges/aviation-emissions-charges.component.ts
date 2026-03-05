import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

@Component({
  selector: 'app-aviation-emissions-charges',
  templateUrl: './aviation-emissions-charges.component.html',
  standalone: true,
  imports: [GovukComponentsModule, SharedModule, NgIf, NgFor, ReturnToLinkComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AviationEmissionsChargesComponent extends BaseAviationEmissionsComponent {
  form = this.formProvider.chargeOperatorCtrl;

  onSubmit() {
    this.saveDreAndNavigate(
      this.formProvider.getFormValue(),
      'in progress',
      this.form.value.chargeOperator ? '../aviation-emissions-charges-calculate' : '../summary',
    );
  }
}
