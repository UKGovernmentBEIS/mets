import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationEmissionsReasonsFormComponent } from '../aviation-emissions-reasons-form/aviation-emissions-reasons-form.component';
import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

@Component({
  selector: 'app-aviation-emissions-reasons',
  imports: [GovukComponentsModule, SharedModule, ReturnToLinkComponent, AviationEmissionsReasonsFormComponent],
  templateUrl: './aviation-emissions-reasons.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationEmissionsReasonsComponent extends BaseAviationEmissionsComponent {
  form = this.formProvider.determinationReasonCtrl;

  onSubmit() {
    this.saveDreAndNavigate(this.formProvider.getFormValue(), 'in progress', 'aviation-emissions');
  }
}
