import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { SharedModule } from '@shared/shared.module';

import { ProcedureFormComponent } from '../procedure-form';
import { ProcedureFormModel } from './procedure-form.builder';

@Component({
  selector: 'app-procedure-form-step',
  standalone: true,
  imports: [ProcedureFormComponent, SharedModule, ReturnToLinkComponent],
  templateUrl: './procedure-form-step.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ProcedureFormStepComponent {
  @Input() form: FormGroup<ProcedureFormModel>;
  @Input() submitText = 'Continue';
  @Input() hideSubmit = false;
  @Output() readonly submitForm = new EventEmitter<FormGroup<ProcedureFormModel>>();
}
