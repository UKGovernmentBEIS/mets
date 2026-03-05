import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { AviationEmissionsChargesCalculateFormComponent } from '../aviation-emissions-charges-calculate-form/aviation-emissions-charges-calculate-form.component';
import { BaseAviationEmissionsComponent } from '../base-aviation-emissions.component';

@Component({
  selector: 'app-aviation-emissions-charges-calculate',
  templateUrl: './aviation-emissions-charges-calculate.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    AviationEmissionsChargesCalculateFormComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AviationEmissionsChargesCalculateComponent extends BaseAviationEmissionsComponent {
  form = this.formProvider.feeCtrl;

  onSubmit() {
    this.saveDreAndNavigate(this.formProvider.getFormValue(), 'in progress', '../summary');
  }
}
