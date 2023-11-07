import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateReviewed } from '../testing/mock-aer-submitted';
import { OverallDecisionComponent } from './overall-decision.component';

describe('OverallDecisionComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: OverallDecisionComponent;
  let fixture: ComponentFixture<OverallDecisionComponent>;

  class Page extends BasePage<OverallDecisionComponent> {
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
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockStateReviewed);

    fixture = TestBed.createComponent(OverallDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Overall decision');
    expect(page.summaryListValues).toHaveLength(2);
    expect(page.summaryListValues).toEqual([
      ['Decision', 'Not verified'],
      ['Reason', 'the monitoring plan is not approved by the competent authority'],
    ]);
  });
});
