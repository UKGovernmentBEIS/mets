import { inject } from '@angular/core';
import { CanDeactivateFn, Routes } from '@angular/router';

import { AbbreviationsComponent } from '@aviation/request-action/emp/tasks/abbreviations/abbreviations.component';
import { AdditionalDocumentsComponent } from '@aviation/request-action/emp/tasks/additional-documents/additional-documents.component';
import { ApplicationTimeframeComponent } from '@aviation/request-action/emp/tasks/application-timeframe/application-timeframe.component';
import { BlockProceduresComponent } from '@aviation/request-action/emp/tasks/block-procedures/block-procedures.component';
import { DataGapsComponent } from '@aviation/request-action/emp/tasks/data-gaps/data-gaps.component';
import { EmissionSourcesComponent } from '@aviation/request-action/emp/tasks/emission-sources/emission-sources.component';
import { EmissionsReductionClaimComponent } from '@aviation/request-action/emp/tasks/emissions-reduction-claim/emissions-reduction-claim.component';
import { FlightProceduresComponent } from '@aviation/request-action/emp/tasks/flight-procedures/flight-procedures.component';
import { FuelUpliftProceduresComponent } from '@aviation/request-action/emp/tasks/fuel-uplift-procedures/fuel-uplift-procedures.component';
import { ManagementProceduresComponent } from '@aviation/request-action/emp/tasks/management-procedures/management-procedures.component';
import { MethodAProceduresComponent } from '@aviation/request-action/emp/tasks/method-a-procedures/method-a-procedures.component';
import { MethodBProceduresComponent } from '@aviation/request-action/emp/tasks/method-b-procedures/method-b-procedures.component';
import { MonitoringApproachComponent } from '@aviation/request-action/emp/tasks/monitoring-approach/monitoring-approach.component';
import { OperatorDetailsComponent } from '@aviation/request-action/emp/tasks/operator-details/operator-details.component';
import { OverallDecisionComponent } from '@aviation/request-action/emp/tasks/overall-decision/overall-decision.component';
import { ServiceContactDetailsComponent } from '@aviation/request-action/emp/tasks/service-contact-details/service-contact-details.component';

import { RequestActionStore } from '../store';
import { BlockHourProceduresComponent } from './tasks/block-hour-procedures/block-hour-procedures.component';
import { VariationDetailsComponent } from './tasks/variation-details/variation-details.component';

const canDeactivateEmp: CanDeactivateFn<any> = () => {
  inject(RequestActionStore).destroyDelegates();
  return true;
};

export const EMP_ROUTES: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: '/aviation/dashboard',
  },
  {
    path: '',
    canDeactivate: [canDeactivateEmp],
    children: [
      {
        path: 'submitted',
        children: [
          {
            path: 'service-contact-details',
            data: { breadcrumb: 'Service contact details' },
            component: ServiceContactDetailsComponent,
          },
          {
            path: 'operator-details',
            data: { breadcrumb: 'Operator details' },
            component: OperatorDetailsComponent,
          },
          {
            path: 'flight-procedures',
            data: { breadcrumb: 'Flight procedures' },
            component: FlightProceduresComponent,
          },
          {
            path: 'monitoring-approach',
            data: { breadcrumb: 'Monitoring approach' },
            component: MonitoringApproachComponent,
          },
          {
            path: 'emissions-reduction-claim',
            data: { breadcrumb: 'Emissions reduction claim' },
            component: EmissionsReductionClaimComponent,
          },
          {
            path: 'emission-sources',
            data: { breadcrumb: 'Emission sources' },
            component: EmissionSourcesComponent,
          },
          {
            path: 'method-a-procedures',
            data: { breadcrumb: 'Method A procedures' },
            component: MethodAProceduresComponent,
          },
          {
            path: 'method-b-procedures',
            data: { breadcrumb: 'Method B procedures' },
            component: MethodBProceduresComponent,
          },
          {
            path: 'block-on-off-procedures',
            data: { breadcrumb: 'Block on/off procedures' },
            component: BlockProceduresComponent,
          },
          {
            path: 'fuel-uplift-procedures',
            data: { breadcrumb: 'Fuel uplift procedures' },
            component: FuelUpliftProceduresComponent,
          },
          {
            path: 'block-hour-procedures',
            data: { breadcrumb: 'Block hour procedures' },
            component: BlockHourProceduresComponent,
          },
          {
            path: 'data-gaps',
            data: { breadcrumb: 'Data gaps' },
            component: DataGapsComponent,
          },
          {
            path: 'management-procedures',
            data: { breadcrumb: 'Management procedures' },
            component: ManagementProceduresComponent,
          },
          {
            path: 'abbreviations',
            data: { breadcrumb: 'Abbreviations' },
            component: AbbreviationsComponent,
          },
          {
            path: 'additional-docs',
            data: { breadcrumb: 'Additional documents' },
            component: AdditionalDocumentsComponent,
          },
          {
            path: 'application-timeframe-apply',
            data: { breadcrumb: 'Application timeframe' },
            component: ApplicationTimeframeComponent,
          },
          {
            path: 'variation-details',
            data: { breadcrumb: 'Describe the changes' },
            component: VariationDetailsComponent,
          },
          {
            path: 'decision',
            component: OverallDecisionComponent,
          },
        ],
      },
    ],
  },
];
