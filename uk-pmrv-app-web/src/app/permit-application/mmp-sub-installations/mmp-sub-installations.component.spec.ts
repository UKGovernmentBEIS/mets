import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, RouterLink } from '@angular/router';

import { of } from 'rxjs';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { SubInstallationTypesService } from 'pmrv-api';

import { MmpSubInstallationsComponent } from './mmp-sub-installations.component';

describe('MmpSubInstallationsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;
  let component: MmpSubInstallationsComponent;
  let fixture: ComponentFixture<MmpSubInstallationsComponent>;
  let subInstallationTypesService: Partial<jest.Mocked<SubInstallationTypesService>>;

  class Page extends BasePage<MmpSubInstallationsComponent> {
    get tables() {
      return this.queryAll<HTMLTableElement>('govuk-table');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(MmpSubInstallationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    subInstallationTypesService = {
      getSubInstallationTypesDetails: jest.fn().mockReturnValue(
        of([
          {
            subInstallationType: 'PRIMARY_ALUMINIUM',
            valid: true,
            coveredByUKCBAM: true,
          },
          {
            subInstallationType: 'HEAT_BENCHMARK_CL',
            valid: true,
            coveredByUKCBAM: true,
          },
        ]),
      ),
    };

    await TestBed.configureTestingModule({
      declarations: [MmpSubInstallationsComponent],
      imports: [SharedModule, SharedPermitModule, RouterLink],
      providers: [
        provideRouter([]),
        { provide: SubInstallationTypesService, useValue: subInstallationTypesService },
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  describe('without subinstallations', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        competentAuthority: 'OPRED',
        ...mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION: [false],
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the table', () => {
      expect(page.tables.length).toEqual(0);
      expect(page.rows).toEqual([]);
    });
  });

  describe('with product benchmark', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        competentAuthority: 'ENGLAND',
        ...mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
              digitizedPlan: {
                subInstallations: [
                  {
                    subInstallationNo: '0',
                    subInstallationType: 'PRIMARY_ALUMINIUM',
                  },
                ],
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION: [false],
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the table', () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows).toEqual([
        ['[Primary] Aluminium', 'Exposed', 'Remove  [Primary] Aluminium sub-installation', 'In progress'],
      ]);
    });
  });

  describe('with fallback approaches', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        competentAuthority: 'ENGLAND',
        ...mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
              digitizedPlan: {
                subInstallations: [
                  {
                    subInstallationNo: '0',
                    subInstallationType: 'HEAT_BENCHMARK_CL',
                  },
                ],
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            MMP_SUB_INSTALLATION: [false],
          },
        ),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the table', () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows).toEqual([
        [
          'Heat benchmark exposed to carbon leakage',
          'Exposed',
          'Remove  heat benchmark exposed to carbon leakage sub-installation',
          'In progress',
        ],
      ]);
    });
  });
});
