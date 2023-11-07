import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { AuthStore } from '@core/store/auth';
import { BasePage } from '@testing';

import { CommonActionsState } from '../../store/common-actions.state';
import { CommonActionsStore } from '../../store/common-actions.store';
import { AerModule } from '../aer.module';
import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let authStore: AuthStore;

  class Page extends BasePage<CompletedComponent> {
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
    }).compileComponents();

    authStore = TestBed.inject(AuthStore);
    authStore.setUserState({ roleType: 'REGULATOR' });
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 1,
        requestAccountId: 13,
        submitter: 'Operator',
        creationDate: '2022-11-29T12:12:48.469862Z',
        type: 'AER_APPLICATION_COMPLETED',
        payload: {
          payloadType: 'AER_APPLICATION_COMPLETED_PAYLOAD',
          reportingYear: '2022',
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.heading).toEqual('2022 emissions report reviewed');
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([['Emissions report', '2022 emissions report']]);
  });
});
