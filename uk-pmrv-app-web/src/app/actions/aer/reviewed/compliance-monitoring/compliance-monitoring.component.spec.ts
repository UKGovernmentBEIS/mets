import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { ComplianceMonitoringComponent } from './compliance-monitoring.component';

describe('ComplianceMonitoringComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ComplianceMonitoringComponent;
  let fixture: ComponentFixture<ComplianceMonitoringComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'COMPLIANCE_MONITORING_REPORTING',
    },
  );

  class Page extends BasePage<ComplianceMonitoringComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
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

    fixture = TestBed.createComponent(ComplianceMonitoringComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Compliance with monitoring and reporting principles');
    expect(page.summaryListValues).toHaveLength(8);
    expect(page.summaryListValues).toEqual([
      ['Accuracy', 'Yes'],
      ['Completeness', 'Yes'],
      ['Consistency', 'Yes'],
      ['Comparability over time', 'Yes'],
      ['Transparency', 'Yes'],
      ['Integrity of methodology', 'Yes'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
