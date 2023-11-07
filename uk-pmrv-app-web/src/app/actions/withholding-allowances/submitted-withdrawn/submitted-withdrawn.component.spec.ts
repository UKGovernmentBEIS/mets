import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { CommonActionsStore } from '../../store/common-actions.store';
import { WithholdingAllowancesModule } from '../withholding-allowances.module';
import { SubmittedWithdrawnComponent } from './submitted-withdrawn.component';
import { mockState } from './testing/mock-wa-submitted';

describe('SubmittedWithdrawnComponent', () => {
  let page: Page;
  let store: CommonActionsStore;
  let component: SubmittedWithdrawnComponent;
  let fixture: ComponentFixture<SubmittedWithdrawnComponent>;

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Withholding allowances',
    actionType: 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED',
  });

  class Page extends BasePage<SubmittedWithdrawnComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }

    get summary(): string {
      return this.query<HTMLElement>('app-wa-summary-template').textContent.trim();
    }

    get data() {
      return this.queryAll<HTMLElement>('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithholdingAllowancesModule, RouterTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState(mockState);

    fixture = TestBed.createComponent(SubmittedWithdrawnComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.heading).toEqual('Withholding allowances');
    expect(page.summary).toBeTruthy();
    expect(page.data).toEqual([
      'Withholding of allowances details',
      'From which year are these allowances being withheld?',
      '2023',
      '',
      'Reason for withholding allowances',
      'Assessing transfer of aviation free allocation under article 34Q',
      '',
      'Official notice recipients',
      'Official notice',
      '',
    ]);
  });
});
