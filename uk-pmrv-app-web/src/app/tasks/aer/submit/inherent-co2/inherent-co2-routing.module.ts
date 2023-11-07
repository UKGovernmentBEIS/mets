import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { DeleteComponent } from '@tasks/aer/submit/inherent-co2/delete/delete.component';
import { DetailsComponent } from '@tasks/aer/submit/inherent-co2/details/details.component';
import { DirectionComponent } from '@tasks/aer/submit/inherent-co2/direction/direction.component';
import { EmissionsComponent } from '@tasks/aer/submit/inherent-co2/emissions/emissions.component';
import { WizardStepGuard } from '@tasks/aer/submit/inherent-co2/guards/wizard-step.guard';
import { InherentCo2Component } from '@tasks/aer/submit/inherent-co2/inherent-co2.component';
import { InstrumentsComponent } from '@tasks/aer/submit/inherent-co2/instruments/instruments.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Inherent CO2' },
    component: InherentCo2Component,
  },
  {
    path: ':index',
    children: [
      {
        path: 'direction',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 - Direction', backlink: '../..' },
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
