import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormArray, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '@permit-application/shared/permit-task-form.token';
import { ProductBenchmarkComponent } from '@permit-application/shared/product-benchmark/product-benchmark.component';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';

import { SubInstallationTypesService } from 'pmrv-api';

import { isFallbackApproach } from '../mmp-sub-installations-status';
import { fallbackApproachAddFormFactory } from './fallback-approach-details-form.provider';

@Component({
  selector: 'app-sub-installation-fallback-details',
  standalone: false,
  templateUrl: './sub-installation-fallback-details.component.html',
  providers: [fallbackApproachAddFormFactory],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubInstallationFallbackDetailsComponent extends ProductBenchmarkComponent implements PendingRequest {
  isEditing$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('subInstallationNo') != null));
  permitTask$ = this.route.data.pipe(map((x) => x?.permitTask));

  vm$: Observable<any> = combineLatest([
    this.store.getTask('monitoringMethodologyPlans'),
    this.subInstallationTypesService.getSubInstallationTypesDetails(),
  ]).pipe(
    map(([monitoringMethodologyPlans, subInstallationDetails]) => {
      return {
        heatBenchmark: [
          'HEAT_BENCHMARK_CL',
          'HEAT_BENCHMARK_CL_CBAM',
          'HEAT_BENCHMARK_CL_NON_CBAM',
          'HEAT_BENCHMARK_NON_CL',
        ].filter((heat) =>
          subInstallationDetails.some((item) => item['subInstallationType'] === heat && item['valid']),
        ) as any[],
        fuelBenchmark: [
          'FUEL_BENCHMARK_CL',
          'FUEL_BENCHMARK_CL_CBAM',
          'FUEL_BENCHMARK_CL_NON_CBAM',
          'FUEL_BENCHMARK_NON_CL',
        ].filter((fuel) =>
          subInstallationDetails.some((item) => item['subInstallationType'] === fuel && item['valid']),
        ) as any[],
        processEmissions: [
          'PROCESS_EMISSIONS_CL',
          'PROCESS_EMISSIONS_CL_CBAM',
          'PROCESS_EMISSIONS_CL_NON_CBAM',
          'PROCESS_EMISSIONS_NON_CL',
        ].filter((proccess) =>
          subInstallationDetails.some((item) => item['subInstallationType'] === proccess && item['valid']),
        ) as any[],
        fallbackApproachFallbackTypes: monitoringMethodologyPlans?.digitizedPlan?.subInstallations
          ?.filter(
            (subInstallation) =>
              isFallbackApproach(subInstallation.subInstallationType) &&
              subInstallationDetails.some(
                (item) => item['subInstallationType'] === subInstallation.subInstallationType && item['valid'],
              ) &&
              this.form.value?.subInstallationType !== subInstallation.subInstallationType,
          )
          .map((fallbackApproach) => fallbackApproach.subInstallationType),
      };
    }),
  );
  subInstallationNo: string;

  fallbackLabels = {
    HEAT_BENCHMARK_CL: 'Exposed',
    HEAT_BENCHMARK_NON_CL: 'Not exposed',
    HEAT_BENCHMARK_CL_CBAM: 'Exposed (covered by UK CBAM)',
    HEAT_BENCHMARK_CL_NON_CBAM: 'Exposed (not covered by UK CBAM)',
    FUEL_BENCHMARK_CL: 'Exposed',
    FUEL_BENCHMARK_NON_CL: 'Not exposed',
    FUEL_BENCHMARK_CL_CBAM: 'Exposed (covered by UK CBAM)',
    FUEL_BENCHMARK_CL_NON_CBAM: 'Exposed (not covered by UK CBAM)',
    PROCESS_EMISSIONS_CL: 'Exposed',
    PROCESS_EMISSIONS_NON_CL: 'Not exposed',
    PROCESS_EMISSIONS_CL_CBAM: '(Exposed, covered by UK CBAM)',
    PROCESS_EMISSIONS_CL_NON_CBAM: '(Exposed, not covered by UK CBAM)',
  };

  readonly isFileUploaded$: Observable<boolean> = this.form.get('supportingFiles').valueChanges.pipe(
    startWith(this.form.get('supportingFiles').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: UntypedFormGroup,
    readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    readonly store: PermitApplicationStore<PermitApplicationState>,
    readonly subInstallationTypesService: SubInstallationTypesService,
  ) {
    super(store, router, route);
  }

  get parentOptionsFormArray(): FormArray {
    return this.form.get('parentOptions') as FormArray;
  }

  isInvalid(controlPath: string): boolean {
    const control = this.form.get(controlPath);
    return control && control.invalid && (control.dirty || control.touched);
  }

  getDownloadUrl() {
    return this.store.createBaseFileAttachmentDownloadUrl();
  }

  onSubmit(): void {
    const formValue = { ...this.form.value };
    delete formValue?.subInstallationTypeOptions;

    combineLatest([this.permitTask$, this.store, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([permitTask, state, paramMap]) => {
          const digitizedPlan = state.permit?.monitoringMethodologyPlans?.digitizedPlan;
          const subInstallations = digitizedPlan && digitizedPlan?.subInstallations;

          const fallbackApproachIndex = subInstallations
            ?.filter((subInstallation) => isFallbackApproach(subInstallation.subInstallationType))
            .findIndex((x) => x.subInstallationNo == paramMap.get('subInstallationNo'));

          this.subInstallationNo =
            paramMap?.get('subInstallationNo') != null
              ? paramMap?.get('subInstallationNo')
              : subInstallations
                ? subInstallations?.length + ''
                : 0 + '';

          return this.store
            .patchTask(
              permitTask,
              {
                ...state.permit.monitoringMethodologyPlans,
                digitizedPlan: {
                  ...state.permit.monitoringMethodologyPlans?.digitizedPlan,
                  subInstallations: subInstallations?.some(
                    (item) => item.subInstallationNo === paramMap.get('subInstallationNo'),
                  )
                    ? subInstallations?.map((item, index) => {
                        if (
                          item.subInstallationNo === paramMap.get('subInstallationNo') &&
                          item?.subInstallationType !== this.form.value?.subInstallationType
                        ) {
                          delete item?.annualLevel;
                          delete item?.directlyAttributableEmissions;
                          delete item?.fuelInputAndRelevantEmissionFactor;
                          delete item?.measurableHeat;
                        }

                        return item.subInstallationNo === paramMap.get('subInstallationNo')
                          ? {
                              ...item,
                              ...formValue,
                              supportingFiles: formValue?.supportingFiles?.map((file) => file.uuid),
                              subInstallationNo: paramMap.get('subInstallationNo'),
                            }
                          : {
                              ...item,
                              subInstallationNo: '' + index,
                            };
                      })
                    : subInstallations && subInstallations?.length
                      ? [
                          ...subInstallations.map((item, index) => {
                            return { ...item, subInstallationNo: '' + index };
                          }),
                          {
                            ...formValue,
                            supportingFiles: formValue?.supportingFiles?.map((file) => file.uuid),
                            subInstallationNo: (subInstallations?.length ?? 0) + '',
                          },
                        ]
                      : [
                          {
                            ...formValue,
                            supportingFiles: formValue?.supportingFiles?.map((file) => file.uuid),
                            subInstallationNo: (subInstallations?.length ?? 0) + '',
                          },
                        ],
                },
              },

              fallbackApproachIndex !== -1 &&
                state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']?.length
                ? state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach']?.map((item, idx) =>
                    fallbackApproachIndex === idx ? false : item,
                  )
                : state.permitSectionsCompleted?.['MMP_SUB_INSTALLATION_Fallback_Approach']
                  ? [...state.permitSectionsCompleted['MMP_SUB_INSTALLATION_Fallback_Approach'], false]
                  : [false],
              'MMP_SUB_INSTALLATION_Fallback_Approach',
            )
            .pipe(this.pendingRequest.trackRequest());
        }),
        first(),
        switchMap(() => combineLatest([this.isEditing$, this.productBenchmarkType$])),
      )
      .subscribe(([isEditing, productBenchmarkType]) => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...formValue?.supportingFiles?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });

        let nextStep = '../';
        switch (productBenchmarkType) {
          case 'HEAT_BENCHMARK_CL':
          case 'HEAT_BENCHMARK_CL_CBAM':
          case 'HEAT_BENCHMARK_CL_NON_CBAM':
          case 'HEAT_BENCHMARK_NON_CL':
          case 'DISTRICT_HEATING_NON_CL':
            nextStep = isEditing
              ? 'annual-activity-levels-heat'
              : `../fallback/${this.subInstallationNo}/annual-activity-levels-heat`;
            break;
          case 'FUEL_BENCHMARK_CL':
          case 'FUEL_BENCHMARK_CL_CBAM':
          case 'FUEL_BENCHMARK_CL_NON_CBAM':
          case 'FUEL_BENCHMARK_NON_CL':
            nextStep = isEditing
              ? 'annual-activity-levels-fuel'
              : `../fallback/${this.subInstallationNo}/annual-activity-levels-fuel`;
            break;
          case 'PROCESS_EMISSIONS_CL':
          case 'PROCESS_EMISSIONS_CL_CBAM':
          case 'PROCESS_EMISSIONS_CL_NON_CBAM':
          case 'PROCESS_EMISSIONS_NON_CL':
            nextStep = isEditing
              ? 'annual-activity-levels-process'
              : '../fallback/' + this.subInstallationNo + '/annual-activity-levels-process';
            break;
          default:
            nextStep = isEditing ? '../../' : '../';
        }

        this.router.navigate([`./${nextStep}`], { relativeTo: this.route });
      });
  }
}
