import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { takeUntil, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { DestroySubject } from '@core/services/destroy-subject.service';
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

  private readonly wastePermitEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('wastePermitEnabled'));

  ngOnInit(): void {
    this.wastePermitEnabled$
      .pipe(
        takeUntil(this.destroy$),
        tap((wastePermitEnabled) => {
          this.typeOptions.forEach((option) => {
            this.sourceStreamTypesArray.push({ label: this.sourceStreamType.transform(option), value: option });
          });
          this.sourceStreamTypesArray.sort((a, b) => (a.label > b.label ? 1 : -1));

          if (!wastePermitEnabled) {
            this.descriptionOptions = this.descriptionOptions.filter(
              (option) =>
                option !== 'CLINICAL_WASTE' &&
                option !== 'COMMERCIAL_INDUSTRIAL_WASTE' &&
                option !== 'HAZARDOUS_WASTE' &&
                option !== 'RDF' &&
                option !== 'SRF',
            );
          }
        }),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.formSubmit.emit(this.form);
  }

  constructor(
    private readonly configStore: ConfigStore,
    private readonly destroy$: DestroySubject,
  ) {}
}
