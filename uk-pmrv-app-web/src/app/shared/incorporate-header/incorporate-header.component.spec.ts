import { ComponentFixture, TestBed } from '@angular/core/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore } from '@core/store/auth';
import { InstallationCategoryPipe } from '@permit-application/shared/pipes/installation-category.pipe';
import { AccountStatusPipe } from '@shared/pipes/account-status.pipe';
import { AviationNamePipePipe } from '@shared/pipes/aviation-name-pipe.pipe';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { BasePage, MockType } from '@testing';

import { AviationAccountViewService, InstallationAccountViewService } from 'pmrv-api';

import { mockedAccountHeaderInfo, mockedAviationAccountHeaderInfo } from '../../accounts/testing/mock-data';
import { IncorporateHeaderComponent } from './incorporate-header.component';
import { IncorporateHeaderState } from './store/incorporate-header.state';
import { IncorporateHeaderStore } from './store/incorporate-header.store';

describe('IncorporateHeaderComponent', () => {
  let component: IncorporateHeaderComponent;
  let fixture: ComponentFixture<IncorporateHeaderComponent>;
  let store: IncorporateHeaderStore;
  let page: Page;
  let authStore: AuthStore;

  let accountViewService: MockType<InstallationAccountViewService>;
  let aviationAccountViewService: MockType<AviationAccountViewService>;

  class Page extends BasePage<IncorporateHeaderComponent> {
    get candidateAssigneesSelectInputValue(): HTMLSelectElement {
      return this.query<HTMLSelectElement>('.govuk-phase-banner');
    }
  }

  const mockTaskState: IncorporateHeaderState = { accountId: 1 };

  function createComponent() {
    fixture = TestBed.createComponent(IncorporateHeaderComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    accountViewService = {
      getInstallationAccountHeaderInfoById: jest.fn().mockReturnValue(of(mockedAccountHeaderInfo)),
    };
    aviationAccountViewService = {
      getAviationAccountHeaderInfoById: jest.fn().mockReturnValue(of(mockedAviationAccountHeaderInfo)),
    };
    await TestBed.configureTestingModule({
      providers: [
        { provide: InstallationAccountViewService, useValue: accountViewService },
        { provide: AviationAccountViewService, useValue: aviationAccountViewService },
        DestroySubject,
      ],
      imports: [SharedModule, TaskSharedModule],
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
  });

  beforeEach(() => {
    store = TestBed.inject(IncorporateHeaderStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  describe('installation account id has value', () => {
    beforeEach(() => {
      authStore.setCurrentDomain('INSTALLATION');
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display incorporate header', () => {
      const accountStatus = new AccountStatusPipe();
      const status = accountStatus.transform(mockedAccountHeaderInfo.status);

      const installationCategory = new InstallationCategoryPipe();
      const category = installationCategory.transform(mockedAccountHeaderInfo.installationCategory);

      expect(page.candidateAssigneesSelectInputValue.textContent).toEqual(
        ` ${mockedAccountHeaderInfo.name}  Permit ID:  ${mockedAccountHeaderInfo.permitId} / ${status}  Type:  ${mockedAccountHeaderInfo.emitterType} / ${category} `,
      );
    });
  });

  describe('account id has no value', () => {
    beforeEach(() => {
      store.setState({ ...store.getState(), accountId: undefined });
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });
    it('should not display incorporate header', () => {
      accountViewService.getInstallationAccountHeaderInfoById.mockReturnValue(of(null));

      expect(page.candidateAssigneesSelectInputValue).toEqual(null);
    });
  });

  describe('aviation account id has value', () => {
    beforeEach(() => {
      authStore.setCurrentDomain('AVIATION');
      createComponent();
    });
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display incorporate header', () => {
      const accountStatus = new AccountStatusPipe();
      const status = accountStatus.transform(mockedAviationAccountHeaderInfo.status);

      const emissionTradingScheme = new AviationNamePipePipe();
      const scheme = emissionTradingScheme.transform(mockedAviationAccountHeaderInfo.emissionTradingScheme);

      expect(page.candidateAssigneesSelectInputValue.textContent).toEqual(
        ` ${mockedAviationAccountHeaderInfo.name}  Emissions Plan ID:  ${mockedAviationAccountHeaderInfo.empId} / ${status}  Schema: ${scheme} `,
      );
    });
  });
});
