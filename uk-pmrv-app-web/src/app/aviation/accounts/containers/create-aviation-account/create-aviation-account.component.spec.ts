import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { firstValueFrom } from 'rxjs';

import { ActivatedRouteStub, mockClass } from '@testing';

import {
  AviationAccountCreationDTO,
  AviationAccountReportingStatusService,
  AviationAccountsService,
  AviationAccountUpdateService,
} from 'pmrv-api';

import { AviationAccountFormProvider } from '../../services';
import { AviationAccountsStore, selectIsInitiallySubmitted, selectNewAccount } from '../../store';
import { CreateAviationAccountComponent } from './create-aviation-account.component';

describe('AccountComponent', () => {
  let component: CreateAviationAccountComponent;
  let fixture: ComponentFixture<CreateAviationAccountComponent>;
  let store: AviationAccountsStore;

  const account = {
    name: 'TESTNAME',
    emissionTradingScheme: 'CORSIA' as AviationAccountCreationDTO['emissionTradingScheme'],
    sopId: null,
    crcoCode: 'TESTCRCO',
    commencementDate: 'TEST_DATE',
  };

  const accountsService: Partial<AviationAccountsService> = {
    createAviationAccount: jest.fn(),
    isExistingAccountName1: jest.fn(),
    isExistingCrcoCode: jest.fn(),
  };
  const router: Partial<Router> = {
    navigate: jest.fn(),
  };
  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [CreateAviationAccountComponent],
      providers: [
        AviationAccountsStore,
        AviationAccountFormProvider,
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: AviationAccountReportingStatusService, useValue: mockClass(AviationAccountReportingStatusService) },
        { provide: AviationAccountUpdateService, useValue: mockClass(AviationAccountUpdateService) },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: route },
      ],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateAviationAccountComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(AviationAccountsStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should set newAccount and isInitiallySubmitted in store when submitting the form', async () => {
    component.form.setValue({ account });
    component.onContinue();

    const iis = await firstValueFrom(store.pipe(selectIsInitiallySubmitted));
    const na = await firstValueFrom(store.pipe(selectNewAccount));
    expect(iis).toEqual(true);
    expect(na).toEqual(account);
    expect(router.navigate).toHaveBeenCalledWith(['summary'], { relativeTo: route });
  });
});
