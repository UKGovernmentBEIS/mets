import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { AviationAccountFormProvider } from '@aviation/accounts/services';
import { AviationAccountsStore } from '@aviation/accounts/store';
import { mockedAccount } from '@aviation/accounts/testing/mock-data';
import { SharedModule } from '@shared/shared.module';

import { EditCommencementDateAviationAccountComponent } from './edit-commencement-date-aviation-account.component';

describe('EditCommencementDateAviationAccountComponent', () => {
  let component: EditCommencementDateAviationAccountComponent;
  let fixture: ComponentFixture<EditCommencementDateAviationAccountComponent>;
  let store: AviationAccountsStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [EditCommencementDateAviationAccountComponent],
      providers: [AviationAccountsStore, AviationAccountFormProvider, provideRouter([]), provideHttpClient()],
    }).compileComponents();

    store = TestBed.inject(AviationAccountsStore);
    store.setCurrentAccount(mockedAccount);

    fixture = TestBed.createComponent(EditCommencementDateAviationAccountComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should patch form with account info', () => {
    const expectedFormValue = { commencementDate: new Date('2023-01-01') as any, reason: null };

    component.ngOnInit();

    expect(component.form.value).toEqual(expectedFormValue);
  });
});
