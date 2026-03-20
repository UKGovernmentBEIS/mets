import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

@Component({
  selector: 'app-aviation-emissions-charges',
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  templateUrl: './aviation-emissions-charges.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
