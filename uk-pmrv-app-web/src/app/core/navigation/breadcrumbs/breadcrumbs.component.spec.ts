import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukComponentsModule } from 'govuk-components';

import { BreadcrumbsComponent } from './breadcrumbs.component';
import { BREADCRUMB_ITEMS } from './breadcrumbs.factory';

@Component({ standalone: false, template: '' })
class DummyComponent {}

describe('BreadcrumbsComponent', () => {
  let fixture: ComponentFixture<BreadcrumbsComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BreadcrumbsComponent, DummyComponent],
      imports: [
        GovukComponentsModule,
        RouterTestingModule.withRoutes([
          { path: 'dashboard', data: { breadcrumb: 'Dashboard' }, component: DummyComponent },
          {
            path: 'accounts',
            data: { breadcrumb: 'Accounts' },
            children: [{ path: 'detail', data: { breadcrumb: 'Account detail' }, component: DummyComponent }],
          },
        ]),
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(BreadcrumbsComponent);
    fixture.detectChanges();
  });

  it('suppresses the breadcrumb trail on a single-level, top-level page', async () => {
    await router.navigate(['/dashboard']);
    fixture.detectChanges();

    expect(TestBed.inject(BREADCRUMB_ITEMS).getValue()).toEqual([]);
    expect(fixture.nativeElement.querySelector('govuk-breadcrumbs')).toBeFalsy();
  });

  it('keeps the ancestor chain on a deeper page, dropping only the current page itself', async () => {
    await router.navigate(['/accounts/detail']);
    fixture.detectChanges();

    const breadcrumbs = TestBed.inject(BREADCRUMB_ITEMS).getValue();

    expect(breadcrumbs).toEqual([{ text: 'Accounts', link: ['/accounts'], queryParams: {} }]);
    expect(fixture.nativeElement.querySelector('govuk-breadcrumbs')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Accounts');
    expect(fixture.nativeElement.textContent).not.toContain('Account detail');
  });
});
