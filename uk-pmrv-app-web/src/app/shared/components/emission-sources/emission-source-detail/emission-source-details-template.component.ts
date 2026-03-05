import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

@Component({
  selector: 'app-emission-source-details-template',
  templateUrl: './emission-source-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmissionSourceDetailsTemplateComponent {
  @Input() form: UntypedFormGroup;
  @Input() isEditing: boolean;
  @Input() caption: string;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
