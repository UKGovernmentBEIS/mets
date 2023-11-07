import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { descriptionOptions, typeOptions } from '@shared/components/source-streams/source-stream-options';
import { SourceStreamTypePipe } from '@shared/pipes/source-streams-type.pipe';

import { SourceStream } from 'pmrv-api';

export interface SourceStreamOption {
  label: string;
  value: SourceStream['type'];
}

@Component({
  selector: 'app-source-streams-details-template',
  templateUrl: './source-streams-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SourceStreamDetailsTemplateComponent implements OnInit {
  @Input() form: UntypedFormGroup;
  @Input() isEditing: boolean;

  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  descriptionOptions = descriptionOptions;
  typeOptions = typeOptions;
  sourceStreamType = new SourceStreamTypePipe();
  sourceStreamTypesArray: Array<SourceStreamOption> = [];

  ngOnInit(): void {
    typeOptions.forEach((option) => {
      this.sourceStreamTypesArray.push({ label: this.sourceStreamType.transform(option), value: option });
    });
    this.sourceStreamTypesArray.sort((a, b) => (a.label > b.label ? 1 : -1));
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }
}
