import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockState } from '../testing/mock-aer-submitted';
import { ConfidentialityStatementComponent } from './confidentiality-statement.component';

describe('ConfidentialityStatementComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ConfidentialityStatementComponent;
  let fixture: ComponentFixture<ConfidentialityStatementComponent>;

  class Page extends BasePage<ConfidentialityStatementComponent> {
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
    store.setState(mockState);

    fixture = TestBed.createComponent(ConfidentialityStatementComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Confidentiality statement');
    expect(page.summaryListValues).toHaveLength(4);
    expect(page.summaryListValues).toEqual([
      ['Section', 'Section 1'],
      ['Explanation', 'Explanation 1'],
      ['Section', 'Section 2'],
      ['Explanation', 'Explanation 2'],
    ]);
  });
});
