import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { mockStateBuild } from '@permit-application/testing/mock-state';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { ConnectionsComponent } from './connections.component';

describe('ConnectionsComponent', () => {
  let component: ConnectionsComponent;
  let fixture: ComponentFixture<ConnectionsComponent>;
  let page: Page;
  let store: PermitApplicationStore<PermitApplicationState>;

  class Page extends BasePage<ConnectionsComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Continue',
      );
    }

    get addConnectionBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add an installation or entity',
      );
    }

    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((data) =>
        Array.from(data.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConnectionsComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [
        {
          provide: PermitApplicationStore,
          useExisting: PermitIssuanceStore,
        },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ConnectionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for adding connection', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
              digitizedPlan: {
                installationDescription: {
                  description: 'description',
                  flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
                },
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            mmpInstallationDescription: [false],
          },
        ),
      );
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new measurement device button ', () => {
      expect(page.addConnectionBtn).toBeTruthy();
      expect(page.summaryListValues).toEqual([]);
    });
  });

  describe('for existing connections', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitIssuanceStore);
      store.setState(
        mockStateBuild(
          {
            monitoringMethodologyPlans: {
              exist: true,
              plans: ['e227ea8a-778b-4208-9545-e108ea66c114'],
              digitizedPlan: {
                installationDescription: {
                  description: 'description',
                  flowDiagrams: ['e227ea8a-778b-4208-9545-e108ea66c113'],
                  connections: [
                    {
                      entityType: 'ETS_INSTALLATION',
                      phoneNumber: '1234567890',
                      connectionNo: '0',
                      emailAddress: '1@1',
                      flowDirection: 'IMPORT',
                      connectionType: 'MEASURABLE_HEAT',
                      installationId: '123',
                      contactPersonName: '1',
                      installationOrEntityName: 'name1',
                    },
                  ],
                },
              },
            },
          },
          {
            monitoringMethodologyPlans: [true],
            mmpInstallationDescription: [false],
          },
        ),
      );
    });

    beforeEach(createComponent);

    it('should show connections', () => {
      expect(page.addConnectionBtn).toBeTruthy();
      expect(page.summaryListValues).toEqual([
        [
          '',
          'Remove',
          'Installation covered by ETS',
          'Change',
          'Measurable heat',
          'Change',
          'Import: something entering the boundaries of your installation',
          'Change',
          '123',
          'Change',
          '1',
          'Change',
          '1@1',
          'Change',
          '1234567890',
          'Change',
        ],
      ]);
    });
  });
});
