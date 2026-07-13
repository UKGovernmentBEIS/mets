import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import {
  newSubInstallationNameLabelsMap,
  subInstallationNameLabelsMap,
  subInstallationNameLabelsMap2027,
} from '@shared/components/doal/activity-level-label.map';

@Component({
  selector: 'app-preliminary-allocation-details-template',
  standalone: false,
  templateUrl: './preliminary-allocation-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreliminaryAllocationDetailsTemplateComponent implements OnInit {
  @Input() form: UntypedFormGroup;
  @Input() isEditable: boolean;
  @Input() isEditing: boolean;
  @Input() submitText: string = 'Save and continue';
  @Input() newAllocationHeading: string = 'Allocation - new item';
  @Input() isAlr: boolean = false;
  @Input() year: number;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  readonly years = Array.from({ length: 2035 - 2021 + 1 }, (_, i) => 2021 + i).map((year) => ({
    text: `${year}`,
    value: `${year}`,
  }));
  subInstallationNameLabelsMap;

  ngOnInit(): void {
    this.subInstallationNameLabelsMap = this.isAlr
      ? this.year >= 2027
        ? subInstallationNameLabelsMap2027
        : subInstallationNameLabelsMap
      : newSubInstallationNameLabelsMap;
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
