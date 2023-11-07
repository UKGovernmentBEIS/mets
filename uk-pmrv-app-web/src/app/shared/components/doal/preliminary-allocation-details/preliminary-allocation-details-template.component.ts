import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { subInstallationNameLabelsMap } from '@shared/components/doal/activity-level-label.map';

@Component({
  selector: 'app-preliminary-allocation-details-template',
  templateUrl: './preliminary-allocation-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreliminaryAllocationDetailsTemplateComponent {
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() isEditing: boolean;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  readonly years = Array.from({ length: 2035 - 2021 + 1 }, (_, i) => 2021 + i).map((year) => ({
    text: `${year}`,
    value: `${year}`,
  }));
  subInstallationNameLabelsMap = subInstallationNameLabelsMap;

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
