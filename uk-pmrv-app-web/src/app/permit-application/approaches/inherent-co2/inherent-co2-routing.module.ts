import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { DirectionComponent } from './direction/direction.component';
import { EmissionsComponent } from './emissions/emissions.component';
import { WizardStepGuard } from './guards/wizard-step.guard';
import { InherentCO2Component } from './inherent-co2.component';
import { InstrumentsComponent } from './instruments/instruments.component';
import { InherentSummaryComponent } from './summary/inherent-summary.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Inherent CO2' },
    component: InherentCO2Component,
  },
  {
    path: 'summary',
    data: {
      pageTitle: 'Monitoring approaches - Inherent CO2 approach description - Summary',
      breadcrumb: 'Inherent CO2',
    },
    component: InherentSummaryComponent,
    canActivate: [SummaryGuard],
  },
  {
    path: ':index',
    data: { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2' },
    children: [
      {
        path: 'direction',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Direction' },
        component: DirectionComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'details',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Details', backlink: '../direction' },
        component: DetailsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'instruments',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Instruments', backlink: '../details' },
        component: InstrumentsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'emissions',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Emissions', backlink: '../instruments' },
        component: EmissionsComponent,
        canActivate: [WizardStepGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Delete', backlink: '../..' },
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
export class InherentCo2RoutingModule {}
