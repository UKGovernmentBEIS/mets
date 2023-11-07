import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { CalculationOfCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { deleteReturnUrl } from '../../../approaches';

@Component({
  selector: 'app-category-tier-delete',
  template: `
    <app-page-heading size="xl">
      Are you sure you want to delete
      <span class="nowrap"> ‘{{ index$ | async | sourceStreamCategoryName: 'CALCULATION_CO2' | async }}’? </span>
    </app-page-heading>

    <p class="govuk-body">All the information within this source stream category will be deleted.</p>

    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="delete()" govukWarnButton>Yes, delete</button>
      <a routerLink=".." govukLink>Cancel</a>
    </div>
  `,
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent implements PendingRequest {
  index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('index'))),
        withLatestFrom(this.store, this.route.data),
        switchMap(([index, state, data]) =>
          this.store.postCategoryTask(data.taskKey, {
            ...state,
            permit: {
              ...state.permit,
              monitoringApproaches: {
                ...state.permit.monitoringApproaches,
                CALCULATION_CO2: {
                  ...state.permit.monitoringApproaches.CALCULATION_CO2,
                  sourceStreamCategoryAppliedTiers:
                    (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
                      .sourceStreamCategoryAppliedTiers.length > 1
                      ? (
                          state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach
                        ).sourceStreamCategoryAppliedTiers.filter((_, i) => i !== index)
                      : null,
                } as CalculationOfCO2MonitoringApproach,
              },
            },
            permitSectionsCompleted: {
              ...state.permitSectionsCompleted,
              CALCULATION_CO2_Category: state.permitSectionsCompleted.CALCULATION_CO2_Category?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Emission_Factor: state.permitSectionsCompleted.CALCULATION_CO2_Emission_Factor?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Biomass_Fraction: state.permitSectionsCompleted.CALCULATION_CO2_Biomass_Fraction?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Carbon_Content: state.permitSectionsCompleted.CALCULATION_CO2_Carbon_Content?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Oxidation_Factor: state.permitSectionsCompleted.CALCULATION_CO2_Oxidation_Factor?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Calorific: state.permitSectionsCompleted.CALCULATION_CO2_Calorific?.filter(
                (_, i) => i !== index,
              ),
              CALCULATION_CO2_Conversion_Factor:
                state.permitSectionsCompleted.CALCULATION_CO2_Conversion_Factor?.filter((_, i) => i !== index),
              CALCULATION_CO2_Activity_Data: state.permitSectionsCompleted.CALCULATION_CO2_Activity_Data?.filter(
                (_, i) => i !== index,
              ),
            },
          }),
        ),
        switchMap(() => this.store),
        first(),
        this.pendingRequest.trackRequest(),
      )
      .subscribe((state) => this.router.navigate([deleteReturnUrl(state, 'calculation')], { relativeTo: this.route }));
  }
}
