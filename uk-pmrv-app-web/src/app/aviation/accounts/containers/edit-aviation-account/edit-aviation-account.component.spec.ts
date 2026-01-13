import { provideHttpClient } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { EditAviationAccountComponent } from '@aviation/accounts/containers';
import { mockedAccount } from '@aviation/accounts/testing/mock-data';

import { AviationAccountFormProvider } from '../../services';
import { AviationAccountsStore } from '../../store';

describe('EditAviationAccountComponent', () => {
  let component: EditAviationAccountComponent;
  let fixture: ComponentFixture<EditAviationAccountComponent>;
  let store: AviationAccountsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditAviationAccountComponent],
      providers: [AviationAccountsStore, AviationAccountFormProvider, provideRouter([]), provideHttpClient()],
      schemas: [NO_ERRORS_SCHEMA, CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);

    fixture = TestBed.createComponent(EditAviationAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should patch form with account info', () => {
    const expectedFormValue = {
      account: {
        name: 'TEST',
        emissionTradingScheme: 'CORSIA',
        crcoCode: 'TEST',
        sopId: '3',
        id: 1,
        hasContactAddress: [false],
        location: {
          type: 'ONSHORE_STATE',
          line1: null,
          line2: null,
          city: null,
          country: null,
          state: null,
          postcode: null,
        },
      },
    };

    component.ngOnInit();

    expect(component.form.value).toEqual(expectedFormValue);
  });
});
