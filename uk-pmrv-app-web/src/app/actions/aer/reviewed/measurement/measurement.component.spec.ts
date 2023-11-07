import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { MeasurementComponent } from './measurement.component';

describe('MeasurementComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: MeasurementComponent;
  let fixture: ComponentFixture<MeasurementComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'MEASUREMENT_CO2',
      taskKey: 'MEASUREMENT_CO2',
    },
  );

  class Page extends BasePage<MeasurementComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
      imports: [AerModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateCompleted);

    fixture = TestBed.createComponent(MeasurementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Measurement of CO2 emissions');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['EP1', 'the reference Anthracite', 'emission source 1 reference', '-37.026', '0', ''],
      ['Total emissions', '', '', '-37.026 tCO2e', '0 tCO2e', ''],
    ]);
  });
});
