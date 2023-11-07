import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { DateRangeComponent } from '../../shared/components/submit/date-range/date-range.component';
import { DeleteComponent } from '../../shared/components/submit/delete/delete.component';
import { TiersReasonComponent } from '../../shared/components/submit/tiers-reason/tiers-reason.component';
import { ActivityCalculationAggregationComponent } from './activity-calculation/activity-calculation-aggregation/activity-calculation-aggregation.component';
import { ActivityCalculationContinuousComponent } from './activity-calculation/activity-calculation-continuous/activity-calculation-continuous.component';
import { ActivityCalculationMethodComponent } from './activity-calculation-method/activity-calculation-method.component';
import { BiomassCalculationComponent } from './biomass-calculation/biomass-calculation.component';
import { CalculationEmissionsComponent } from './calculation-emissions.component';
import { CalculationMethodComponent } from './calculation-method/calculation-method.component';
import { InventoryDataReviewComponent } from './calculation-review/inventory-data-review/inventory-data-review.component';
import { ManualDataReviewComponent } from './calculation-review/manual-data-review/manual-data-review.component';
import { ConditionsMeteredComponent } from './conditions-metered/conditions-metered.component';
import { DeliveryZoneComponent } from './delivery-zone/delivery-zone.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { SummaryGuard } from './guards/summary.guard';
import { WizardStepGuard } from './guards/wizard-step.guard';
import { InstallationPostcodeComponent } from './installation-postcode/installation-postcode.component';
import { ManualCalculationValuesComponent } from './manual-calculation-values/manual-calculation-values.component';
import { RelevantCategoryComponent } from './relevant-category/relevant-category.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';
import { TransferredComponent } from './transferred/transferred.component';
import { TransferredDetailsComponent } from './transferred-details/transferred-details.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Calculation approach' },
    component: CalculationEmissionsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':index',
    children: [
      {
        path: 'emission-network',
        data: { pageTitle: 'Define the emission network' },
        component: EmissionNetworkComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'transferred',
        data: { pageTitle: 'Transferred CO2' },
        component: TransferredComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'transferred-details',
        data: { pageTitle: 'Transferred details' },
        component: TransferredDetailsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'date-range',
        data: {
          pageTitle: 'What date range does this entry cover?',
          taskKey: 'CALCULATION_CO2',
          backlink: '../emission-network',
        },
        component: DateRangeComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'tiers-used',
        data: {
          pageTitle: 'What tiers have you used for this source stream’s calculation parameters?',
          backlink: '../date-range',
        },
        component: TiersUsedComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'tiers-reason',
        data: {
          pageTitle: 'Why could you not use the tiers applied in your monitoring plan?',
          taskKey: 'CALCULATION_CO2',
          backlink: '../tiers-used',
        },
        component: TiersReasonComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'calculation-method',
        data: { pageTitle: 'How do you want to calculate the parameter values for this source stream?' },
        component: CalculationMethodComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'installation-postcode',
        data: { pageTitle: 'How do you want to calculate the parameter values for this source stream?' },
        component: InstallationPostcodeComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delivery-zone',
        data: { pageTitle: 'Find your local delivery zone' },
        component: DeliveryZoneComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'relevant-category',
        data: { pageTitle: 'Select the relevant category for the main activity at the installation' },
        component: RelevantCategoryComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'conditions-metered',
        data: { pageTitle: 'What conditions was the gas for this source stream metered?' },
        component: ConditionsMeteredComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'activity-calculation-method',
        data: {
          pageTitle: 'How do you want to calculate the activity data for this source stream?',
          backlink: '../tiers-used',
        },
        component: ActivityCalculationMethodComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'activity-calculation-aggregation',
        data: {
          pageTitle: 'Calculate the activity data for this source stream',
          backlink: '../activity-calculation-method',
        },
        component: ActivityCalculationAggregationComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'activity-calculation-continuous',
        data: {
          pageTitle: 'Calculate the activity data for this source stream',
          backlink: '../activity-calculation-method',
        },
        component: ActivityCalculationContinuousComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'biomass-calculation',
        data: { pageTitle: 'Calculate the biomass values for this source stream' },
        component: BiomassCalculationComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'manual-calculation-values',
        data: {
          pageTitle: 'Provide the emission calculation values for this source stream',
          backlink: '../activity-calculation-continuous',
        },
        component: ManualCalculationValuesComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'inventory-data-review',
        data: {
          pageTitle: 'Review the calculated emissions and values applied by the UK greenhouse gas inventory data',
        },
        component: InventoryDataReviewComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'manual-data-review',
        data: {
          pageTitle: 'Review the calculated emissions',
          backlink: '../manual-calculation-values',
        },
        component: ManualDataReviewComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'Calculation emissions' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Are you sure you want to delete this source stream?', taskKey: 'CALCULATION_CO2' },
        component: DeleteComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CalculationEmissionsRoutingModule {}
