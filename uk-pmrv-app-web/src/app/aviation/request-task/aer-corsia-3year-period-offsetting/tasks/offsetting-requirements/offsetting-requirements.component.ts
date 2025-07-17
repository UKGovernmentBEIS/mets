import {
  ChangeDetectionStrategy,
  Component,
  computed,
  Inject,
  OnInit,
  Signal,
  signal,
  WritableSignal,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReturnToLinkComponent } from '@aviation/shared/components/return-to-link';
import { AviationAerCorsia3YearPeriodOffsettingTableData } from '@aviation/shared/types';
import { getTableData } from '@aviation/shared/utils/3year-period-offsetting-shared.util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { SharedModule } from '@shared/shared.module';

import {
  AviationAerCorsia3YearPeriodOffsetting,
  AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData,
} from 'pmrv-api';

import { ThreeYearOffsettingRequirementsTableTemplateComponent } from '../../../../shared/components/offsetting-requirements-table-template/offsetting-requirements-table-template.component';
import { ThreeYearOffsettingRequirementsFormProvider } from '../../aer-corsia-3year-period-offsetting-form.provider';

@Component({
  selector: 'app-3year-offsetting-requirements',
  standalone: true,
  imports: [ReturnToLinkComponent, SharedModule, ThreeYearOffsettingRequirementsTableTemplateComponent],
  templateUrl: './offsetting-requirements.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThreeYearOffsettingRequirementsComponent implements OnInit {
  form = this.formProvider.form.get('offsettingRequirements') as FormGroup<
    Record<keyof AviationAerCorsia3YearPeriodOffsetting, FormControl>
  >;
  totalYearlyOffsettingData: WritableSignal<AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> = signal({
    calculatedAnnualOffsetting: 0,
    cefEmissionsReductions: 0,
  });
  periodOffsettingRequirements: WritableSignal<AviationAerCorsia3YearPeriodOffsettingYearlyOffsettingData> = signal({
    calculatedAnnualOffsetting: null,
    cefEmissionsReductions: 0,
  });
  caption = `${this.form.value.schemeYears[0]} - ${this.form.value.schemeYears[this.form.value.schemeYears.length - 1]} offsetting requirements`;
  data: Signal<AviationAerCorsia3YearPeriodOffsettingTableData[]> = computed(() => {
    return getTableData(this.form.value as AviationAerCorsia3YearPeriodOffsetting);
  });

  constructor(
    @Inject(TASK_FORM_PROVIDER) public formProvider: ThreeYearOffsettingRequirementsFormProvider,
    private readonly store: RequestTaskStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequestService: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.form
      .get('yearlyOffsettingData')
      .valueChanges.subscribe((data: AviationAerCorsia3YearPeriodOffsetting['yearlyOffsettingData']) => {
        const formValues = this.form.value as AviationAerCorsia3YearPeriodOffsetting;
        const sumCalculatedAnnualOffsetting =
          this.getNumber(+data[formValues.schemeYears[0]].calculatedAnnualOffsetting) +
          this.getNumber(+data[formValues.schemeYears[1]].calculatedAnnualOffsetting) +
          this.getNumber(+data[formValues.schemeYears[2]].calculatedAnnualOffsetting);

        const sumCefEmissionsReductions =
          this.getNumber(+data[formValues.schemeYears[0]].cefEmissionsReductions) +
          this.getNumber(+data[formValues.schemeYears[1]].cefEmissionsReductions) +
          this.getNumber(+data[formValues.schemeYears[2]].cefEmissionsReductions);

        const totalYearlyOffsettingData = {
          calculatedAnnualOffsetting: sumCalculatedAnnualOffsetting,
          cefEmissionsReductions: sumCefEmissionsReductions,
        };

        const periodOffsettingRequirementsCalculation =
          totalYearlyOffsettingData.calculatedAnnualOffsetting - totalYearlyOffsettingData.cefEmissionsReductions;

        this.form.get('totalYearlyOffsettingData').patchValue(totalYearlyOffsettingData);

        this.totalYearlyOffsettingData.set(totalYearlyOffsettingData);
        this.periodOffsettingRequirements.set({
          calculatedAnnualOffsetting: null,
          cefEmissionsReductions:
            periodOffsettingRequirementsCalculation > 0 ? periodOffsettingRequirementsCalculation : 0,
        });
        this.form
          .get('periodOffsettingRequirements')
          .patchValue(periodOffsettingRequirementsCalculation > 0 ? periodOffsettingRequirementsCalculation : 0);
      });
  }

  onSubmit() {
    this.store.aerCorsia3YearPeriodOffsetting
      .saveOffsettingRequirement(this.form.value as AviationAerCorsia3YearPeriodOffsetting, 'in progress')
      .pipe(this.pendingRequestService.trackRequest())
      .subscribe(() => {
        this.router.navigate(['summary'], {
          relativeTo: this.route,
        });
      });
  }

  private getNumber(value: number): number {
    return isNaN(value) ? 0 : value;
  }
}
