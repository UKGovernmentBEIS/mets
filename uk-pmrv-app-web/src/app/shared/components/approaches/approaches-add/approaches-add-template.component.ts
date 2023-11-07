import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'app-approaches-add-template',
  templateUrl: './approaches-add-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesAddTemplateComponent {
  @Input() form: UntypedFormGroup;
  @Input() monitoringApproaches: { [key: string]: any };
  @Input() submitText = 'Save and continue';
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
