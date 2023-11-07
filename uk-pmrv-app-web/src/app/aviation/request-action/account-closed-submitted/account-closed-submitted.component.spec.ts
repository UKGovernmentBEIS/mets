import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { AccountClosureRequestActionPayload, RequestActionStore } from '../store';
import { AccountClosedSubmittedComponent } from './account-closed-submitted.component';

describe('AccountClosedSubmittedComponent', () => {
  let component: AccountClosedSubmittedComponent;
  let fixture: ComponentFixture<AccountClosedSubmittedComponent>;
  let page: Page;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();

  class Page extends BasePage<AccountClosedSubmittedComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }

    get summaryValues() {
      return this.queryAll<HTMLDListElement>('dd');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, SharedModule, AccountClosedSubmittedComponent],
      declarations: [],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED',
        creationDate: '2023-06-08T13:22:00Z',
        payload: {
          aviationAccountClosure: {
            reason: 'Reason provided',
          },
        } as AccountClosureRequestActionPayload,
        submitter: 'Regulator Name',
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(AccountClosedSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.header).toEqual('Account closed');
    expect(page.summaryValues.map((dd) => dd.textContent)).toEqual([
      'Reason provided',
      'Regulator Name',
      '8 Jun 2023, 2:22pm',
    ]);
  });
});
