import { Routes } from '@angular/router';

export const SUBSIDIARY_COMPANY_ROUTES: Routes = [
  {
    path: 'list',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./operator-details-subsidiary-companies.component').then(
        (c) => c.OperatorDetailsSubsidiaryCompaniesComponent,
      ),
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'has-subsidiary-companies',
  },
  {
    path: 'has-subsidiary-companies',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./has-subsidiary-company/has-subsidiary-company.component').then((c) => c.HasSubsidiaryCompanyComponent),
  },
  {
    path: 'add',
    data: { backlink: '../' },
    loadComponent: () =>
      import('./add-subsidiary-company/add-subsidiary-company.component').then((c) => c.AddSubsidiaryCompanyComponent),
  },
  {
    path: ':index/edit',
    data: { backlink: '../../' },
    loadComponent: () =>
      import('./add-subsidiary-company/add-subsidiary-company.component').then((c) => c.AddSubsidiaryCompanyComponent),
  },
];
