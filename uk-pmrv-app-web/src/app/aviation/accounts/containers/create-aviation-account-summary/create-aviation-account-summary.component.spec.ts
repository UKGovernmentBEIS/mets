import { AsyncPipe } from '@angular/common';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { firstValueFrom, of } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@testing';

import { AviationAccountReportingStatusService, AviationAccountsService, AviationAccountUpdateService } from 'pmrv-api';

import { AviationAccountsStore, selectIsSubmitted } from '../../store';
import { CreateAviationAccountSummaryComponent } from './create-aviation-account-summary.component';

describe('CreateAviationAccountSummaryComponent', () => {
  let component: CreateAviationAccountSummaryComponent;
  let fixture: ComponentFixture<CreateAviationAccountSummaryComponent>;
  let store: AviationAccountsStore;

  const accountsService: Partial<AviationAccountsService> = {
    createAviationAccount: jest.fn().mockReturnValue(of({ res: 200 })),
    isExistingAccountName1: jest.fn(),
    isExistingCrcoCode: jest.fn(),
  };
  const router: Partial<Router> = {
    navigate: jest.fn(),
  };
  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateAviationAccountSummaryComponent],
      providers: [
        AviationAccountsStore,
        AsyncPipe,
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: route },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateAviationAccountSummaryComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(AviationAccountsStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call createAccount and set IsSubmitted in store when submitting the form', async () => {
    component.onSubmit();
    await fixture.whenStable();
    const is = await firstValueFrom(store.pipe(selectIsSubmitted));
    expect(is).toEqual(true);
    expect(accountsService.createAviationAccount).toHaveBeenCalled();
  });
});
