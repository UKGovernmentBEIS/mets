import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { PermitMonitoringApproachSection } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { approachesAddFormProvider } from './approaches-add-form.provider';

@Component({
  selector: 'app-approaches-add',
  template: `
    <app-permit-task [breadcrumb]="[{ text: 'Define monitoring approaches', link: ['monitoring-approaches'] }]">
      <app-approaches-add-template
        (formSubmit)="onSubmit()"
        [monitoringApproaches]="monitoringApproaches$ | async"
        [form]="form"
      >
        <ng-container heading>
          <p class="govuk-body">Select the monitoring approaches relevant to your installation.</p>
          <p class="govuk-body">
            Get help with
            <a govukLink routerLink="../help-with-monitoring-approaches" target="_blank">monitoring approaches</a>.
          </p>
        </ng-container>
        <ng-container returnTo>
          <a govukLink routerLink="..">Return to: Define monitoring approaches</a>
        </ng-container>
      </app-approaches-add-template>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [approachesAddFormProvider],
})
export class ApproachesAddComponent {
  monitoringApproaches$ = this.store
    .getTask('monitoringApproaches')
    .pipe(map((monitoringApproaches) => monitoringApproaches ?? {}));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    const approachesUnchecked: string[] = monitoringApproachTypeOptions.filter((option) => {
      if (option === 'TRANSFERRED_CO2_N2O') {
        return (
          !this.form.value.hasTransferCalculationCO2 &&
          !this.form.value.hasTransferMeasurementCO2 &&
          !this.form.value.hasTransferMeasurementN2O
        );
      } else {
        return !this.form.value.monitoringApproaches.includes(option);
      }
    });

    const calculationApproach = this.form.value.monitoringApproaches.includes('CALCULATION_CO2')
      ? this.buildApproach('CALCULATION_CO2', this.form.value?.hasTransferCalculationCO2)
      : {};
    const measurementCO2Approach = this.form.value.monitoringApproaches.includes('MEASUREMENT_CO2')
      ? this.buildApproach('MEASUREMENT_CO2', this.form.value?.hasTransferMeasurementCO2)
      : {};
    const measurementN2OApproach = this.form.value.monitoringApproaches.includes('MEASUREMENT_N2O')
      ? this.buildApproach('MEASUREMENT_N2O', this.form.value?.hasTransferMeasurementN2O)
      : {};
    const inherentCo2Approach = this.form.value.monitoringApproaches.includes('INHERENT_CO2')
      ? this.buildApproach('INHERENT_CO2', undefined)
      : {};
    const pfcApproach = this.form.value.monitoringApproaches.includes('CALCULATION_PFC')
      ? this.buildApproach('CALCULATION_PFC', undefined)
      : {};
    const fallbackApproach = this.form.value.monitoringApproaches.includes('FALLBACK')
      ? this.buildApproach('FALLBACK', undefined)
      : {};
    const transferredCo2Approach =
      this.form.value?.hasTransferCalculationCO2 ||
      this.form.value?.hasTransferMeasurementCO2 ||
      this.form.value?.hasTransferMeasurementN2O
        ? this.buildApproach('TRANSFERRED_CO2_N2O', undefined)
        : {};

    //TODO make 1 call
    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postCategoryTask(
            'monitoringApproaches',
            {
              ...state,
              permitSectionsCompleted: {
                ...Object.keys(state.permitSectionsCompleted)
                  .filter((key) => !approachesUnchecked.some((element) => key.startsWith(element)))
                  .reduce((res, key) => ({ ...res, [key]: state.permitSectionsCompleted[key] }), {}),
              },
            },
            {
              ...Object.keys(state.reviewSectionsCompleted)
                .filter((key) => !approachesUnchecked.some((element) => key.startsWith(element)))
                .reduce((res, key) => ({ ...res, [key]: state.reviewSectionsCompleted[key] }), {}),
              DEFINE_MONITORING_APPROACHES: false,
              ...(state?.determination?.type !== 'DEEMED_WITHDRAWN' ? { determination: false } : null),
            },
          ),
        ),
        switchMap(() =>
          this.store.postTask(
            'monitoringApproaches',
            {
              ...calculationApproach,
              ...measurementCO2Approach,
              ...measurementN2OApproach,
              ...inherentCo2Approach,
              ...pfcApproach,
              ...fallbackApproach,
              ...transferredCo2Approach,
            },
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }

  buildApproach(monitorApproach: string, hasTransferForm: boolean) {
    return {
      [monitorApproach]: {
        ...this.store.permit.monitoringApproaches?.[monitorApproach],
        type: monitorApproach,
        ...(hasTransferForm !== undefined ? { hasTransfer: hasTransferForm } : {}),
        ...(['CALCULATION_CO2', 'MEASUREMENT_CO2', 'MEASUREMENT_N2O'].some((element) =>
          monitorApproach.includes(element),
        ) &&
          this.store.permit.monitoringApproaches?.[monitorApproach] &&
          this.updateTransferredCO2DataOnSourceStreamCategoryAppliedTiers(
            this.store.permit.monitoringApproaches,
            monitorApproach,
            hasTransferForm,
          )),
      },
    };
  }

  private updateTransferredCO2DataOnSourceStreamCategoryAppliedTiers(
    monitoringApproaches: PermitMonitoringApproachSection,
    monitorApproach: any,
    newHasTransferValue: boolean,
  ) {
    const currentHasTransferValue = (monitoringApproaches[monitorApproach] as any)?.hasTransfer;

    let result = {};

    const categoryAppliedTiers =
      monitorApproach === 'CALCULATION_CO2'
        ? (monitoringApproaches[monitorApproach] as any)?.sourceStreamCategoryAppliedTiers
        : (monitoringApproaches[monitorApproach] as any)?.emissionPointCategoryAppliedTiers;
    if (currentHasTransferValue === true && newHasTransferValue === false) {
      result = this.removeTransferredCO2FromSourceStreamCategoryAppliedTiers(categoryAppliedTiers, monitorApproach);
    } else if (currentHasTransferValue === false && newHasTransferValue === true) {
      result = this.addTransferredCO2FromSourceStreamCategoryAppliedTiers(categoryAppliedTiers, monitorApproach);
    }

    return result;
  }

  private removeTransferredCO2FromSourceStreamCategoryAppliedTiers(categoryAppliedTiers, monitorApproach): any {
    const categoryKey = monitorApproach === 'CALCULATION_CO2' ? 'sourceStreamCategory' : 'emissionPointCategory';
    const categoryAppliedTiersKey =
      monitorApproach === 'CALCULATION_CO2' ? 'sourceStreamCategoryAppliedTiers' : 'emissionPointCategoryAppliedTiers';

    const res = categoryAppliedTiers?.reduce((acc, categoryAppliedTier) => {
      const { transfer, ...rest } = categoryAppliedTier?.[categoryKey];
      const updatedCategoryAppliedTiers = {
        ...categoryAppliedTier,
        [categoryKey]: {
          ...rest,
        },
      };
      acc.push(updatedCategoryAppliedTiers);
      return acc;
    }, []);
    return { [categoryAppliedTiersKey]: res };
  }

  private addTransferredCO2FromSourceStreamCategoryAppliedTiers(categoryAppliedTiers, monitorApproach): any {
    const categoryKey = monitorApproach === 'CALCULATION_CO2' ? 'sourceStreamCategory' : 'emissionPointCategory';
    const categoryAppliedTiersKey =
      monitorApproach === 'CALCULATION_CO2' ? 'sourceStreamCategoryAppliedTiers' : 'emissionPointCategoryAppliedTiers';

    const res = categoryAppliedTiers?.map((categoryAppliedTier) => {
      return {
        ...categoryAppliedTier,
        [categoryKey]: {
          ...categoryAppliedTier?.[categoryKey],
          transfer: {
            entryAccountingForTransfer: false,
            transferType: monitorApproach === 'MEASUREMENT_N2O' ? 'TRANSFER_N2O' : 'TRANSFER_CO2',
          },
        },
      };
    });

    return { [categoryAppliedTiersKey]: res };
  }
}
