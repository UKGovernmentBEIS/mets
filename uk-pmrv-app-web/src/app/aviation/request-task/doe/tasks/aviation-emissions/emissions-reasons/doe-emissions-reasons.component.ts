import { NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { DoeDeterminationReasonSubTypePipe } from '@aviation/shared/pipes/doe-determination-reason-subtype.pipe';
import { DoeDeterminationReasonTypePipe } from '@aviation/shared/pipes/doe-determination-reason-type.pipe';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';

import { GovukComponentsModule } from 'govuk-components';

import { BaseDoeCorsiaEmissionsComponent } from '../doe-corsia-base.component';

@Component({
  selector: 'app-doe-emissions-reasons',
  templateUrl: './doe-emissions-reasons.component.html',
  standalone: true,
  imports: [
    GovukComponentsModule,
    SharedModule,
    NgIf,
    NgFor,
    ReturnToLinkComponent,
    DoeDeterminationReasonTypePipe,
    DoeDeterminationReasonSubTypePipe,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class DoeEmissionsReasonsComponent extends BaseDoeCorsiaEmissionsComponent {
  form = this.formProvider?.determinationReasonCtrl;

  onSubmit() {
    this.saveDoeAndNavigate(this.formProvider.getFormValue(), 'in progress', './estimated-emissions/');
  }
}
