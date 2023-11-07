import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { DateRangeComponent } from '../../shared/components/submit/date-range/date-range.component';
import { DeleteComponent } from '../../shared/components/submit/delete/delete.component';
import { TiersReasonComponent } from '../../shared/components/submit/tiers-reason/tiers-reason.component';
import { CalculationReviewComponent } from './calculation-review/calculation-review.component';
import { EmissionNetworkComponent } from './emission-network/emission-network.component';
import { SummaryGuard } from './guards/summary.guard';
import { WizardStepGuard } from './guards/wizard-step.guard';
import { MethodsComponent } from './methods/methods.component';
import { PfcComponent } from './pfc.component';
import { PrimaryAluminiumComponent } from './primary-aluminium/primary-aluminium.component';
import { SummaryComponent } from './summary/summary.component';
import { TiersUsedComponent } from './tiers-used/tiers-used.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Calculation approach' },
    component: PfcComponent,
    canDeactivate: [PendingRequestGuard],
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
        path: 'date-range',
        data: {
          pageTitle: 'What date range does this entry cover?',
          taskKey: 'CALCULATION_PFC',
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
          pageTitle: 'Reasons for not using the tiers applied in your monitoring plan',
          taskKey: 'CALCULATION_PFC',
          backlink: '../tiers-used',
        },
        component: TiersReasonComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },

      {
        path: 'primary-aluminium',
        data: {
          pageTitle: 'What was the total production of primary aluminium for this source stream',
          backlink: '../tiers-used',
        },
        component: PrimaryAluminiumComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'method-a',
        data: {
          pageTitle: 'Provide the emission calculation values for this source stream',
          methodType: 'METHOD_A',
          backlink: '../primary-aluminium',
        },
        component: MethodsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'method-b',
        data: {
          pageTitle: 'Provide the emission calculation values for this source stream',
          methodType: 'METHOD_B',
          backlink: '../primary-aluminium',
        },
        component: MethodsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'calculation-review',
        data: { pageTitle: 'Review the calculated emissions data?', backlink: 'primary-aluminium' },
        component: CalculationReviewComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers', breadcrumb: 'PFC emissions summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: {
          pageTitle: 'Are you sure you want to delete this source stream?',
          taskKey: 'CALCULATION_PFC',
          backlink: '../',
        },
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
export class PfcRoutingModule {}
