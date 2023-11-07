import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { DeductionsToAmountComponent } from './deductions-to-amount/deductions-to-amount.component';
import { SummaryComponent as DeductionsToAmountSummaryComponent } from './deductions-to-amount/summary/summary.component';
import { AnswersComponent } from './pipeline/answers/answers.component';
import { AnswersGuard } from './pipeline/answers/answers.guard';
import { LeakageComponent } from './pipeline/leakage/leakage.component';
import { PipelineComponent } from './pipeline/pipeline.component';
import { PipelineGuard } from './pipeline/pipeline.guard';
import { SummaryComponent as PipelineSummaryComponent } from './pipeline/summary/summary.component';
import { TemperatureComponent } from './pipeline/temperature/temperature.component';
import { TransferCo2Component } from './pipeline/transfer-co2/transfer-co2.component';
import { StorageComponent } from './storage/storage.component';
import { SummaryComponent as StorageSummaryComponent } from './storage/summary/summary.component';
import { TransferredCO2Component } from './transferred-co2.component';
import { SummaryComponent as TransportApproachSummaryComponent } from './transport-approach/summary/summary.component';
import { TransportApproachComponent } from './transport-approach/transport-approach.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Procedures for transferred CO2 or N2O' },
    component: TransferredCO2Component,
  },
  {
    path: 'deductions',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.deductionsToAmountOfTransferredCO2',
      statusKey: 'TRANSFERRED_CO2_N2O_Deductions',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 deductions to amount', backlink: '../' },
        component: DeductionsToAmountComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 deductions to amount - Summary page' },
        component: DeductionsToAmountSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'approach',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.monitoringTransportNetworkApproach',
      statusKey: 'TRANSFERRED_CO2_N2O_Transport_Network_Approach',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approach for the transport network', backlink: '../deductions' },
        component: TransportApproachComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approach for the transport network - Summary page' },
        component: TransportApproachSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'pipeline',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems',
      statusKey: 'TRANSFERRED_CO2_N2O_Pipeline',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Pipeline systems to transport CO2 or N20', backlink: '../approach' },
        component: PipelineComponent,
        canActivate: [PipelineGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'leakage',
        data: {
          pageTitle: 'Procedure for leakage events',
          taskKey:
            'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems.procedureForLeakageEvents',
        },
        component: LeakageComponent,
      },
      {
        path: 'temperature',
        data: {
          pageTitle: 'Temperature and pressure measurement equipment',
          taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems.temperaturePressure',
        },
        component: TemperatureComponent,
      },
      {
        path: 'transfer-co2',
        data: {
          pageTitle: 'Transfer of CO2',
          taskKey:
            'monitoringApproaches.TRANSFERRED_CO2_N2O.transportCO2AndN2OPipelineSystems.proceduresForTransferredCO2AndN2O',
        },
        component: TransferCo2Component,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approach for the pipeline - Summary page' },
        component: PipelineSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Monitoring approach for the pipeline - Answers page' },
        canActivate: [AnswersGuard],
        component: AnswersComponent,
      },
    ],
  },
  {
    path: 'storage',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2_N2O.geologicalStorage',
      statusKey: 'TRANSFERRED_CO2_N2O_Storage',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Geological storage of CO2 or N20', backlink: '../pipeline' },
        component: StorageComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Geological storage of CO2 or N20 - Summary page' },
        component: StorageSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransferredCO2RoutingModule {}
