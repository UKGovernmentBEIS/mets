import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockState } from '../testing/mock-aer-apply-action';
import { mockStateBuild } from '../testing/mock-state';
import { PfcComponent } from './pfc.component';
import { PfcModule } from './pfc.module';

describe('PfcComponent', () => {
  let page: Page;
  let component: PfcComponent;
  let fixture: ComponentFixture<PfcComponent>;
  let store: CommonTasksStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<PfcComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get addSourceStreamButton() {
      return Array.from(this.queryAll<HTMLAnchorElement>('#emptyCalculation > a')).find(
        (button) => button.textContent.trim() === 'Add a source stream',
      );
    }

    get addAnotherEntryButton() {
      return Array.from(this.queryAll<HTMLAnchorElement>('#nonEmptyCalculation > a')).find(
        (button) => button.textContent.trim() === 'Add another source stream',
      );
    }

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
    fixture = TestBed.createComponent(PfcComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PfcModule, RouterTestingModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  describe('for empty source stream emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachEmissions: { CALCULATION_CO2: null } }));
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display "Pfcemissions" as a heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('PFC emissions');
    });

    it('should display "add a source stream" button and hide "add another entry" button', async () => {
      expect(page.addSourceStreamButton).toBeTruthy();
      expect(page.addAnotherEntryButton).toBeFalsy();
    });
  });

  describe('for non empty source stream emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display "Pfcof emissions" as a heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('PFC emissions');
    });

    it('should display "add another entry" button and hide "add a source stream', async () => {
      expect(page.addSourceStreamButton).toBeFalsy();
      expect(page.addAnotherEntryButton).toBeTruthy();
    });

    it('should fill the table with data', async () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows.length).toEqual(2);
      expect(page.rows).toEqual([
        ['the reference Anthracite', 'emission source 1 reference', 'Slope', '11332812', 'completed'],
        ['Total emissions', '', '', '11332812 tCO2e', ''],
      ]);
    });
  });
});
