import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { PermitRoute } from '../../permit-route.interface';
import { AnswersComponent } from '../../shared/emissions/answers/answers.component';
import { AnswersGuard } from '../../shared/emissions/answers/answers.guard';
import { EmissionsComponent } from '../../shared/emissions/emissions.component';
import { EmissionsGuard } from '../../shared/emissions/emissions.guard';
import { JustificationComponent } from '../../shared/emissions/justification/justification.component';
import { SummaryComponent } from '../../shared/emissions/summary/summary.component';
import { SummaryGuard } from '../../shared/summary.guard';
import { AppliedStandardComponent } from './category-tier/applied-standard/applied-standard.component';
import { SummaryComponent as AppliedStandardSummaryComponent } from './category-tier/applied-standard/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryGuard } from './category-tier/category/summary/category-summary.guard';
import { TransferredCo2DetailsComponent } from './category-tier/category/transferred-co2-details/transferred-co2-details.component';
import { TransferredCO2DetailsGuard } from './category-tier/category/transferred-co2-details/transferred-co2-details.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { MeasurementComponent } from './measurement.component';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as OptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Measurement of CO2' },
    component: MeasurementComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionPointCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Measurement (MSR) emission point category tier' },
        component: CategoryTierComponent,
        canActivate: [CategoryTierGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Are you sure you want to delete this emission point category?', backlink: '../' },
        component: DeleteComponent,
        canActivate: [DeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'category',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Measurement (MSR) emission point category',
              backlink: '../',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Measurement (MSR) emission point category - Summary page' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
          {
            path: 'tranferred-co2-details',
            data: {
              pageTitle: 'Monitoring approaches - Measurement emission point category - TransferredCO2 details page',
            },
            component: TransferredCo2DetailsComponent,
            canActivate: [TransferredCO2DetailsGuard],
          },
        ],
      },
      {
        path: 'applied-standard',
        data: {
          statusKey: 'MEASUREMENT_CO2_Applied_Standard',
        },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Measurement emission point category applied standard',
              backlink: '../category',
            },
            component: AppliedStandardComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Measurement emission point category - summary' },
            component: AppliedStandardSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'emissions',
        data: {
          taskKey: 'MEASUREMENT_CO2',
          statusKey: 'MEASUREMENT_CO2_Measured_Emissions',
        },
        children: [
          {
            path: '',
            data: { pageTitle: 'Measured emissions', backlink: '../applied-standard' },
            component: EmissionsComponent,
            canActivate: [EmissionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'justification',
            data: { pageTitle: 'Measured emissions', backlink: '../' },
            component: JustificationComponent,
            canActivate: [EmissionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Check your answers' },
            component: AnswersComponent,
            canActivate: [AnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Measured emissions' },
            component: SummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_CO2', statusKey: 'MEASUREMENT_CO2_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Measurement approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Measurement approach description - Summary page',
        },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'emissions',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_CO2.emissionDetermination',
      statusKey: 'MEASUREMENT_CO2_Emission',
    },
    children: [
      {
        path: '',
        data: { backlink: '../description' },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'reference',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_CO2.referencePeriodDetermination',
      statusKey: 'MEASUREMENT_CO2_Reference',
    },
    children: [
      {
        path: '',
        data: { backlink: '../emissions' },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'gasflow',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_CO2.gasFlowCalculation', statusKey: 'MEASUREMENT_CO2_Gasflow' },
    children: [
      {
        path: '',
        data: { backlink: '../reference' },
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: OptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'biomass',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_CO2.biomassEmissions', statusKey: 'MEASUREMENT_CO2_Biomass' },
    children: [
      {
        path: '',
        data: { backlink: '../gasflow' },
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: OptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'corroborating',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_CO2.corroboratingCalculations',
      statusKey: 'MEASUREMENT_CO2_Corroborating',
    },
    children: [
      {
        path: '',
        data: { backlink: '../biomass' },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MeasurementRoutingModule {}
