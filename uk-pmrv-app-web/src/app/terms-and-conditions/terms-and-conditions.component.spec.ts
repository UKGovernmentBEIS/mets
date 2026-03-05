import { HttpClientTestingModule, HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, inject, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';
import { SharedModule } from '@shared/shared.module';
import { buttonClick } from '@testing';

import { UsersService } from 'pmrv-api';

import { LandingPageComponent } from '../landing-page/landing-page.component';
import { TermsAndConditionsComponent } from './terms-and-conditions.component';

describe('TermsAndConditionsComponent', () => {
  let component: TermsAndConditionsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let httpTestingController: HttpTestingController;
  let latestTermsStore: LatestTermsStore;
  const authService: Partial<jest.Mocked<AuthService>> = {
    loadUserTerms: jest.fn(() => of({})),
  };

  @Component({
    template: '<app-terms-and-conditions></app-terms-and-conditions>',
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          {
            path: '',
            component: LandingPageComponent,
          },
        ]),
        HttpClientTestingModule,
        SharedModule,
      ],
      providers: [UsersService, { provide: AuthService, useValue: authService }, provideHttpClientTesting()],
      declarations: [TermsAndConditionsComponent, TestComponent, LandingPageComponent],
    }).compileComponents();

    latestTermsStore = TestBed.inject(LatestTermsStore);
    latestTermsStore.setLatestTerms({ url: '/test', version: 2 });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(TermsAndConditionsComponent)).componentInstance;
    fixture.detectChanges();
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have as title Accept terms and conditions', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('h1').textContent).toEqual('Terms And Conditions');
  });

  it('should contain a p tag with body', () => {
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('p')[0].textContent.trim()).toEqual(
      'Below are the Terms and Conditions (Terms) associated with the use of the Manage your UK Emissions Trading Scheme reporting service (METS).',
    );
  });

  it('should enable button when checkbox is checked', () => {
    const compiled = fixture.debugElement.nativeElement;
    const checkbox = fixture.debugElement.queryAll(By.css('input'));
    checkbox[0].nativeElement.click();
    fixture.detectChanges();
    expect(compiled.querySelector('button').disabled).toBeFalsy();
  });

  it('should post if user accepts terms', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const checkbox = fixture.debugElement.query(By.css('input[type=checkbox]'));

    buttonClick(fixture);
    fixture.detectChanges();

    const compiled = fixture.debugElement.nativeElement;
    const errorSummary = compiled.querySelector('govuk-error-message').textContent.trim();
    expect(errorSummary).toEqual('Error: You should accept terms and conditions to proceed');

    checkbox.nativeElement.click();
    fixture.detectChanges();

    buttonClick(fixture);
    fixture.detectChanges();

    const request = httpTestingController.expectOne('http://localhost:8080/api/v1.0/user-terms');
    expect(request.request.method).toEqual('PATCH');
    httpTestingController.verify();

    request.flush(200);
    expect(navigateSpy).toHaveBeenCalledWith(['']);
  }));
});
