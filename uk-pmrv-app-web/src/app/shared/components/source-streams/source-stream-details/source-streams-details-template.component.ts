import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { combineLatest, takeUntil, tap } from 'rxjs';

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
  @Input() showUpstream: boolean = false;
  @Output() readonly formSubmit = new EventEmitter<UntypedFormGroup>();

  descriptionOptions = descriptionOptions;
  typeOptions = typeOptions;
  sourceStreamType = new SourceStreamTypePipe();
  sourceStreamTypesArray: Array<SourceStreamOption> = [];

  private readonly co2VentingEnabled$ = this.configStore.pipe(
    selectIsFeatureEnabled('co2-venting.permit-workflows.enabled'),
  );
  private readonly wastePermitEnabled$ = this.configStore.pipe(selectIsFeatureEnabled('wastePermitEnabled'));

  ngOnInit(): void {
    combineLatest([this.co2VentingEnabled$, this.wastePermitEnabled$])
      .pipe(
        takeUntil(this.destroy$),
        tap(([co2VentingEnabled, wastePermitEnabled]) => {
          if (co2VentingEnabled && this.showUpstream) {
            this.typeOptions = [...typeOptions, 'UPSTREAM_GHG_REMOVAL_VENTING_CO2'];
            this.descriptionOptions = [...descriptionOptions, 'VENTED_GAS'];
            this.descriptionOptions.sort();
          }

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
                option !== 'SDF' &&
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
