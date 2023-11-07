import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { BiomassCalculationComponent } from './biomass-calculation/biomass-calculation.component';
import { CalculationReviewComponent } from './calculation-review/calculation-review.component';
import { DateRangeComponent } from './date-range/date-range.component';
import { DeleteComponent } from './delete/delete.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { GasFlowComponent } from './gas-flow/gas-flow.component';
import { GhgConcentrationComponent } from './ghg-concentration/ghg-concentration.component';
import { SummaryGuard } from './guards/summary.guard';
import { WizardStepGuard } from './guards/wizard-step.guard';
import { MeasurementComponent } from './measurement.component';
import { OperationHoursComponent } from './operation-hours/operation-hours.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersReasonComponent } from './tiers-reason/tiers-reason.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';
import { TransferredComponent } from './transferred/transferred.component';
import { TransferredDetailsComponent } from './transferred-details/transferred-details.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Measurement of CO2 emissions' },
    component: MeasurementComponent,
  },
  {
    path: ':index',
    children: [
      {
        path: 'emission-network',
        data: { pageTitle: 'Define the emission network', backlink: '../..' },
        component: EmissionNetworkComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'transferred',
        data: { pageTitle: 'Transferred emissions', backlink: '../emission-network' },
        component: TransferredComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'transferred-details',
        data: { pageTitle: 'Transferred details', backlink: '../transferred' },
        component: TransferredDetailsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'date-range',
        data: { pageTitle: 'What date range does this entry cover?', backlink: '../emission-network' },
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
          backlink: '../tiers-used',
        },
        component: TiersReasonComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'ghg-concentration',
        data: {
          pageTitle: 'What was the annual hourly average amount of CO2 in the flue gas?',
          backlink: '../operation-hours',
        },
        component: GhgConcentrationComponent,
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
        path: 'operation-hours',
        data: { pageTitle: 'How many hours was this emission network operating?' },
        component: OperationHoursComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'gas-flow',
        data: { pageTitle: 'What was the annual hourly average flue gas flow?', backlink: '../ghg-concentration' },
        component: GasFlowComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'calculation-review',
        data: { pageTitle: 'Review the calculated emissions data?', backlink: '../gas-flow' },
        component: CalculationReviewComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Confirm your answers',
          breadcrumb: (data) => {
            const emissionsType = data.taskKey === 'MEASUREMENT_CO2' ? 'CO2' : 'N2O';
            return `Measurement of ${emissionsType} emissions summary`;
          },
        },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Are you sure you want to delete this source stream?', backlink: '../' },
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
export class MeasurementRoutingModule {}
