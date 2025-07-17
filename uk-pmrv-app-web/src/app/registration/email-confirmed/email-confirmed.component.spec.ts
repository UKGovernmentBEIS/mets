import { ComponentFixture, TestBed } from '@angular/core/testing';

import { mockAuthService } from '@core/guards/mocks';
import { AuthService } from '@core/services/auth.service';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { GovukComponentsModule } from 'govuk-components';

import { UserRegistrationStore } from '../store/user-registration.store';
import { EmailConfirmedComponent } from './email-confirmed.component';

describe('EmailConfirmedComponent', () => {
  let component: EmailConfirmedComponent;
  let fixture: ComponentFixture<EmailConfirmedComponent>;

  let userRegistrationStore: UserRegistrationStore;
  let page: Page;

  class Page extends BasePage<EmailConfirmedComponent> {
    get content() {
      return this.query<HTMLElement>('p').textContent.trim();
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule],
      declarations: [EmailConfirmedComponent],
      providers: [{ provide: AuthService, useValue: mockAuthService }],
    }).compileComponents();

    userRegistrationStore = TestBed.inject(UserRegistrationStore);
    userRegistrationStore.setState({ emailVerificationStatus: 'NOT_REGISTERED', email: 'email@email.gr' });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EmailConfirmedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display create sign in info when verification status is not registered', () => {
    expect(page.content).toEqual('You can continue to create a UK ETS reporting sign in.');
  });

  it('should display create sign in info when verification status is registered', () => {
    userRegistrationStore.setState({ ...userRegistrationStore.getState(), emailVerificationStatus: 'REGISTERED' });
    fixture.detectChanges();
    expect(page.content).toEqual('A sign in already exists for email@email.gr.');
  });
});
