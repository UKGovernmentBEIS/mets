import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'app-emission-point-details-template',
  templateUrl: './emission-point-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionPointDetailsTemplateComponent {
  @Input() form: UntypedFormGroup;
  @Input() isEditing: boolean;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
