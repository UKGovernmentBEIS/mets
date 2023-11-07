import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { SummaryOfConditionsComponent } from '@tasks/aer/review/summary-of-conditions/summary-of-conditions.component';
import { mockState } from '@tasks/aer/review/testing/mock-review';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('SummaryOfConditionsComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
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
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);

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
    expect(page.header.textContent.trim()).toEqual('Summary of conditions, changes, clarifications and variations');
    expect(page.summaryListValues).toEqual([
      ['Yes', ''],
      ['Yes', ''],
      ['Accepted', 'Notes'],
    ]);

    expect(page.rows).toEqual([
      ['A1', 'Explanation A1', '', ''],
      ['B1', 'Explanation B1', '', ''],
    ]);
  });
});
