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
import { TransferredN2ODetailsComponent } from './category-tier/category/transferred-n2o-details/transferred-n2o-details.component';
import { TransferredN2ODetailsGuard } from './category-tier/category/transferred-n2o-details/transferred-n2o-details.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { GasComponent } from './gas/gas.component';
import { SummaryComponent as GasSummaryComponent } from './gas/summary/summary.component';
import { N2oComponent } from './n2o.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Measurement of nitrous oxide (N2O)' },
    component: N2oComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.emissionPointCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission point category tier' },
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
              pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission point category',
              backlink: '../',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission point category - Summary page' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
          {
            path: 'tranferred-co2-details',
            data: {
              pageTitle:
                'Monitoring approaches - MEASUREMENT_N2O emission point category - TransferredCO2 details page',
            },
            component: TransferredN2ODetailsComponent,
            canActivate: [TransferredN2ODetailsGuard],
          },
        ],
      },
      {
        path: 'applied-standard',
        data: {
          statusKey: 'MEASUREMENT_N2O_Applied_Standard',
        },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission point category applied standard',
              backlink: '../category',
            },
            component: AppliedStandardComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission point category - summary' },
            component: AppliedStandardSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'emissions',
        data: {
          taskKey: 'MEASUREMENT_N2O',
          statusKey: 'MEASUREMENT_N2O_Measured_Emissions',
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
            data: { pageTitle: 'Measured emissions' },
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
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_N2O', statusKey: 'MEASUREMENT_N2O_Description' },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) approach description',
          backlink: '../emissions',
        },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) approach description - Summary' },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'emission',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.emissionDetermination',
      statusKey: 'MEASUREMENT_N2O_Emission',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission determination',
          backlink: '../description',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) emission determination - Summary' },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'reference',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.referenceDetermination',
      statusKey: 'MEASUREMENT_N2O_Reference',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of reference period',
          backlink: '../emission',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of reference period - Summary',
        },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'operational',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.operationalManagement',
      statusKey: 'MEASUREMENT_N2O_Operational',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) operational management',
          backlink: '../reference',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) operational management - Summary' },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'emissions',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.nitrousOxideEmissionsDetermination',
      statusKey: 'MEASUREMENT_N2O_Emissions',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of nitrous oxide emissions',
          backlink: '../operational',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of nitrous oxide emissions - Summary',
        },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'concentration',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.nitrousOxideConcentrationDetermination',
      statusKey: 'MEASUREMENT_N2O_Concentration',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of nitrous oxide concentration',
          backlink: '../emissions',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle:
            'Monitoring approaches - Nitrous Oxide (N2O) determination of nitrous oxide concentration - Summary',
        },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'product',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT_N2O.quantityProductDetermination',
      statusKey: 'MEASUREMENT_N2O_Product',
    },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) determination of the quantity of product produced',
          backlink: '../concentration',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle:
            'Monitoring approaches - Nitrous Oxide (N2O) determination of the quantity of product produced - Summary',
        },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'materials',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_N2O.quantityMaterials', statusKey: 'MEASUREMENT_N2O_Materials' },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) quantity of materials',
          backlink: '../product',
        },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) quantity of materials - Summary' },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'gas',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT_N2O.gasFlowCalculation', statusKey: 'MEASUREMENT_N2O_Gas' },
    children: [
      {
        path: '',
        data: {
          pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) calculation of gas flow',
          backlink: '../materials',
        },
        component: GasComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Nitrous Oxide (N2O) calculation of gas flow - Summary' },
        component: GasSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class N2oRoutingModule {}
