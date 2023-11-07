import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { ComplianceEtsComponent } from './compliance-ets.component';

describe('ComplianceEtsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ComplianceEtsComponent;
  let fixture: ComponentFixture<ComplianceEtsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'ETS_COMPLIANCE_RULES',
    },
  );

  class Page extends BasePage<ComplianceEtsComponent> {
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

    fixture = TestBed.createComponent(ComplianceEtsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Compliance with ETS rules');
    expect(page.summaryListValues).toHaveLength(14);
    expect(page.summaryListValues).toEqual([
      ['Monitoring Plan requirements met?', 'Yes'],
      ['EU Regulation on Monitoring and reporting met?', 'Yes'],
      ['Data verified in detail and back to source', 'YesYes detail source data reason'],
      [
        'Control activities are documented, implemented, maintained and effective to mitigate the inherent risks?',
        'Yes',
      ],
      [
        'Procedures listed in monitoring plan are documented, implemented, maintained and effective to mitigate the inherent risks and control risks?',
        'Yes',
      ],
      ['Data verification completed as required?', 'Yes'],
      ['Correct application of monitoring methodology?', 'Yes'],
      ['Reporting of planned or actual changes?', 'Yes'],
      ['Verification of methods applied for missing data?', 'Yes'],
      ['Does the operator need to comply with uncertainty thresholds?', 'Yes'],
      ['Competent authority guidance on monitoring and reporting met?', 'Yes'],
      ['Previous year Non-Conformities corrected?', 'Yes'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);
  });
});
