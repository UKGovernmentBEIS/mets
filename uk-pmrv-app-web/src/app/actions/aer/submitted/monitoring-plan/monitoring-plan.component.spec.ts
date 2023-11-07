import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { MonitoringPlanComponent } from './monitoring-plan.component';

describe('MonitoringPlanComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: MonitoringPlanComponent;
  let fixture: ComponentFixture<MonitoringPlanComponent>;

  class Page extends BasePage<MonitoringPlanComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get planVersions() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(MonitoringPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Monitoring plan versions during the reporting year');

    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([['Changes reported by the operator', 'the details of the deviation']]);

    expect(page.planVersions).toEqual([[], ['UK-W-IN-00013 v1', '14 Dec 2022']]);
  });
});
