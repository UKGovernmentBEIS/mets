import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { mockState } from '../testing/mock-aer-apply-action';
import { mockStateBuild } from '../testing/mock-state';
import { MeasurementComponent } from './measurement.component';
import { MeasurementModule } from './measurement.module';

describe('MeasurementComponent', () => {
  let page: Page;
  let component: MeasurementComponent;
  let fixture: ComponentFixture<MeasurementComponent>;
  let store: CommonTasksStore;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'MEASUREMENT_CO2',
    },
  );

  const tasksService = mockClass(TasksService);
  class Page extends BasePage<MeasurementComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get addEntryButton() {
      return Array.from(this.queryAll<HTMLAnchorElement>('#emptyMeasurement > a')).find(
        (button) => button.textContent.trim() === 'Add a new entry',
      );
    }

    get addAnotherEntryButton() {
      return Array.from(this.queryAll<HTMLAnchorElement>('#nonEmptyMeasurement > a')).find(
        (button) => button.textContent.trim() === 'Add another entry',
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
    fixture = TestBed.createComponent(MeasurementComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],

      imports: [MeasurementModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('for empty emisison point emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ monitoringApproachEmissions: { MEASUREMENT_CO2: null } }));
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display "Measurement CO2 emissions" as a heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Measurement of CO2 emissions');
    });

    it('should display "add a source stream" button and hide "add another entry" button', async () => {
      expect(page.addEntryButton).toBeTruthy();
      expect(page.addAnotherEntryButton).toBeFalsy();
    });
  });

  describe('for non empty MEASUREMENT CO2 emissions', () => {
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

    it('should display "Calculation of emissions" as a heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Measurement of CO2 emissions');
    });

    it('should display "add another entry" button and hide "add a source stream', async () => {
      expect(page.addEntryButton).toBeFalsy();
      expect(page.addAnotherEntryButton).toBeTruthy();
    });

    it('should fill the table with data', async () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows.length).toEqual(2);
      expect(page.rows).toEqual([
        ['EP1', 'the reference Anthracite', 'emission source 1 reference', '-37.026', '0', 'completed'],
        ['Total emissions', '', '', '-37.026 tCO2e', '0 tCO2e', ''],
      ]);
    });
  });
});
