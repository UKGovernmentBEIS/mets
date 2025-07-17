import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions, SourceStreamTypeCategory } from 'pmrv-api';

import { sourceStreamFormProvider } from './source-stream-form.provider';

@Component({
  selector: 'app-source-streams-details',
  template: `
    <app-aer-task>
      <app-source-streams-details-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditing]="isEditing$ | async"></app-source-streams-details-template>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [sourceStreamFormProvider],
})
export class SourceStreamDetailsComponent {
  isEditing$ = this.aerService
    .getTask('sourceStreams')
    .pipe(map((sourceStreams) => sourceStreams?.some((item) => item.id === this.form.get('id').value)));

  payload$ = this.aerService.getPayload();
  sourceStreamCategories$ = this.aerService.getSourceStreamCategories();

  currentSourceStreamId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('streamId')));

  constructor(
    @Inject(AER_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    combineLatest([this.payload$, this.currentSourceStreamId$, this.sourceStreamCategories$])
      .pipe(
        first(),
        switchMap(([payload, currentSourceStreamId, sourceStreamCategories]) => {
          const sourceStreams = payload.aer.sourceStreams;

          return this.aerService.postTaskSave(
            {
              sourceStreams: sourceStreams?.some((item) => item.id === this.form.value.id)
                ? sourceStreams.map((item) => (item.id === this.form.value.id ? this.form.value : item))
                : [...(sourceStreams ?? []), this.form.value],
              ...(this.shouldSourceStreamEmissionsBeUpdated(payload, currentSourceStreamId, sourceStreamCategories)
                ? this.getSourceStreamEmissionsUpdatedData(payload)
                : {}),
            },
            undefined,
            false,
            'sourceStreams',
          );
        }),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }

  private shouldSourceStreamEmissionsBeUpdated(
    payload: AerApplicationSubmitRequestTaskPayload,
    currentSourceStreamId: string,
    sourceStreamCategories: SourceStreamTypeCategory[],
  ): boolean {
    const doRelatedSourceStreamEmissionsExist = !!(
      payload.aer.monitoringApproachEmissions?.CALCULATION_CO2 as CalculationOfCO2Emissions
    )?.sourceStreamEmissions?.some((sourceStreamEmission) => sourceStreamEmission.sourceStream === this.form.value.id);

    const currentSourceStreamCategoryType = payload.aer?.sourceStreams.find(
      (sourceStream) => sourceStream.id === currentSourceStreamId,
    )?.type;
    const currentSourceStreamCategory = sourceStreamCategories.find(
      (sourceStreamCategory) => sourceStreamCategory.type === currentSourceStreamCategoryType,
    )?.category;

    const newSourceStreamCategory = sourceStreamCategories.find(
      (sourceStreamCategory) => sourceStreamCategory.type === this.form.value.type,
    )?.category;

    const hasSourceStreamCategoryTypeChanged =
      currentSourceStreamCategory && currentSourceStreamCategory !== newSourceStreamCategory;

    return doRelatedSourceStreamEmissionsExist && hasSourceStreamCategoryTypeChanged;
  }

  private getSourceStreamEmissionsUpdatedData(payload: AerApplicationSubmitRequestTaskPayload) {
    const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
    const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

    const calculationSectionsCompleted =
      payload?.aerSectionsCompleted?.CALCULATION_CO2 ?? Array(calculation?.sourceStreamEmissions?.length).fill(false);

    const sourceStreamEmissions = calculation?.sourceStreamEmissions.map((sourceStreamEmission, index) => {
      if (sourceStreamEmission.sourceStream === this.form.value.id) {
        calculationSectionsCompleted[index] = false;

        return {
          ...sourceStreamEmission,
          ...(sourceStreamEmission?.parameterCalculationMethod ? { parameterCalculationMethod: null } : {}),
          ...(sourceStreamEmission?.parameterMonitoringTiers ? { parameterMonitoringTiers: null } : {}),
          ...(sourceStreamEmission?.parameterMonitoringTierDiffReason
            ? { parameterMonitoringTierDiffReason: null }
            : {}),
        };
      } else {
        return sourceStreamEmission;
      }
    });

    const data = {
      monitoringApproachEmissions: {
        ...monitoringApproachEmissions,
        CALCULATION_CO2: {
          ...calculation,
          type: 'CALCULATION_CO2',
          sourceStreamEmissions: sourceStreamEmissions,
        },
      },
      aerSectionsCompleted: {
        CALCULATION_CO2: calculationSectionsCompleted,
      },
    };

    return data;
  }
}
