import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { CommonActionsStore } from '../../../../store/common-actions.store';
import { DoalActionModule } from '../../../doal-action.module';
import { mockState } from '../../testing/mock-doal-completed';
import { ResponseComponent } from './response.component';

describe('ResponseComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: ResponseComponent;
  let fixture: ComponentFixture<ResponseComponent>;

  class Page extends BasePage<ResponseComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element?.textContent.trim() ?? ''));
    }
    get tableValues() {
      return this.queryAll<HTMLDListElement>('tr').map((naceCode) =>
        Array.from(naceCode.querySelectorAll('td')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DoalActionModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(ResponseComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('Provide UK ETS Authority response');
    expect(page.summaryListValues).toHaveLength(3);
    expect(page.summaryListValues).toEqual([
      ['When did the Authority respond?', '12 Mar 2023'],
      ['Authority decision', 'Approved with corrections'],
      ['Explanation of Authority decision for notice', 'Decision notice'],
    ]);

    expect(page.tableValues).toEqual([
      [],
      ['2023', 'Aluminium', '100'],
      ['2024', 'Aluminium', '200'],
      [],
      ['2023', '100'],
      ['2024', '200'],
    ]);
  });
});
