import { HttpClientTestingModule } from '@angular/common/http/testing';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { of } from 'rxjs';

import { EditAviationAccountComponent } from '@aviation/accounts/containers';
import { ActivatedRouteStub } from '@testing';

import { AviationAccountsService, LocationOnShoreStateDTO } from 'pmrv-api';

import { AviationAccountFormProvider } from '../../services';
import { AviationAccountDetails, AviationAccountsStore } from '../../store';

describe('EditAviationAccountComponent', () => {
  let component: EditAviationAccountComponent;
  let fixture: ComponentFixture<EditAviationAccountComponent>;
  let store: AviationAccountsStore;

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
      imports: [ReactiveFormsModule, HttpClientTestingModule],
      declarations: [EditAviationAccountComponent],
      providers: [
        AviationAccountsStore,
        AviationAccountFormProvider,
        { provide: AviationAccountsService, useValue: accountsService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: route },
      ],
      schemas: [NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(EditAviationAccountComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(AviationAccountsStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should patch form with account info', () => {
    const aviationAccountDetails: AviationAccountDetails = {
      name: 'Test Account',
      emissionTradingScheme: 'UK_ETS_AVIATION',
      crcoCode: '1234',
      sopId: 1,
      commencementDate: '2023-03-28',
      registryId: 5678,
      id: 1,
      location: {
        line1: 'line1',
        line2: 'line2',
        city: 'city',
        country: 'GR',
        postcode: 'postcode',
      } as LocationOnShoreStateDTO,
    };
    jest.spyOn(store, 'pipe').mockReturnValue(of(aviationAccountDetails));
    const expectedFormValue = {
      account: {
        name: 'Test Account',
        emissionTradingScheme: 'UK_ETS_AVIATION',
        crcoCode: '1234',
        sopId: '1',
        commencementDate: new Date('2023-03-28') as any,
        registryId: '5678',
        id: 1,
        hasContactAddress: [true],
        location: {
          type: 'ONSHORE_STATE',
          line1: 'line1',
          line2: 'line2',
          city: 'city',
          country: 'GR',
          state: null,
          postcode: 'postcode',
        },
      },
    };

    component.ngOnInit();

    expect(component.form.value).toEqual(expectedFormValue);
  });
});
