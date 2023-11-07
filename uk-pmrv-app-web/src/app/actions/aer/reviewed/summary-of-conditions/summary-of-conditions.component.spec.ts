import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { AerModule } from '../../aer.module';
import { mockStateCompleted } from '../testing/mock-aer-completed';
import { SummaryOfConditionsComponent } from './summary-of-conditions.component';

describe('SummaryOfConditionsComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: SummaryOfConditionsComponent;
  let fixture: ComponentFixture<SummaryOfConditionsComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      groupKey: 'SUMMARY_OF_CONDITIONS',
    },
  );

  class Page extends BasePage<SummaryOfConditionsComponent> {
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

    fixture = TestBed.createComponent(SummaryOfConditionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Summary of conditions, changes, clarifications and variations');
    expect(page.summaryListValues).toHaveLength(4);
    expect(page.summaryListValues).toEqual([
      ['Were there any changes approved by the regulator that are not included in a re-issued permit?', 'Yes'],
      [
        'Were there any changes identified during your review that were not reported to the regulator by the end of the reporting period?',
        'Yes',
      ],
      ['Decision status', 'Accepted'],
      ['Notes', 'Notes'],
    ]);

    expect(page.tableValues).toEqual([[], ['A1', 'Explanation A1', '', ''], [], ['B1', 'Explanation B1', '', '']]);
  });
});
