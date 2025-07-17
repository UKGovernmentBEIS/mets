import { Routes } from '@angular/router';

export const SHARED_EMP_ROUTES: Routes = [
  {
    path: 'variation-details',
    data: { breadcrumb: 'Describe the changes' },
    loadComponent: () => import('./tasks').then((c) => c.VariationDetailsComponent),
  },
  {
    path: 'service-contact-details',
    data: { breadcrumb: 'Service contact details' },
    loadComponent: () => import('./tasks').then((c) => c.ServiceContactDetailsComponent),
  },
  {
    path: 'abbreviations',
    data: { breadcrumb: 'Abbreviations' },
    loadComponent: () => import('./tasks').then((c) => c.AbbreviationsComponent),
  },
  {
    path: 'additional-docs',
    data: { breadcrumb: 'Additional documents' },
    loadComponent: () => import('./tasks').then((c) => c.AdditionalDocumentsComponent),
  },
  {
    path: 'method-a-procedures',
    data: { breadcrumb: 'Method A procedures' },
    loadComponent: () => import('./tasks').then((c) => c.MethodAProceduresComponent),
  },
  {
    path: 'method-b-procedures',
    data: { breadcrumb: 'Method B procedures' },
    loadComponent: () => import('./tasks').then((c) => c.MethodBProceduresComponent),
  },
  {
    path: 'block-on-off-procedures',
    data: { breadcrumb: 'Block on/off procedures' },
    loadComponent: () => import('./tasks').then((c) => c.BlockProceduresComponent),
  },
  {
    path: 'fuel-uplift-procedures',
    data: { breadcrumb: 'Fuel uplift procedures' },
    loadComponent: () => import('./tasks').then((c) => c.FuelUpliftProceduresComponent),
  },
  {
    path: 'block-hour-procedures',
    data: { breadcrumb: 'Block hour procedures' },
    loadComponent: () => import('./tasks').then((c) => c.BlockHourProceduresComponent),
  },
  {
    path: 'data-gaps',
    data: { breadcrumb: 'Data gaps' },
    loadComponent: () => import('./tasks').then((c) => c.DataGapsComponent),
  },
  {
    path: 'flight-procedures',
    data: { breadcrumb: 'Flight procedures' },
    loadComponent: () => import('./tasks').then((c) => c.FlightProceduresComponent),
  },
  {
    path: 'monitoring-approach',
    data: { breadcrumb: 'Monitoring approach' },
    loadComponent: () => import('./tasks').then((c) => c.MonitoringApproachComponent),
  },
  {
    path: 'emission-sources',
    data: { breadcrumb: 'Emission sources' },
    loadComponent: () => import('./tasks').then((c) => c.EmissionSourcesComponent),
  },
  {
    path: 'management-procedures',
    data: { breadcrumb: 'Management procedures' },
    loadComponent: () => import('./tasks').then((c) => c.ManagementProceduresComponent),
  },
  {
    path: 'decision',
    loadComponent: () => import('./tasks').then((c) => c.OverallDecisionComponent),
  },
];
