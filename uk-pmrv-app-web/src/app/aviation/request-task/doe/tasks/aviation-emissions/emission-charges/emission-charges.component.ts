import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

@Component({
  selector: 'app-doe-emissions-charges',
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent],
  templateUrl: './emission-charges.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
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
