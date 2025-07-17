import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CommonActionsState } from '@actions/store/common-actions.state';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { VerifierReturnedToOperatorActionComponent } from './verifier-returned-to-operator.component';

describe('VerifierReturnedToOperatorComponent', () => {
  let component: VerifierReturnedToOperatorActionComponent;
  let fixture: ComponentFixture<VerifierReturnedToOperatorActionComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<VerifierReturnedToOperatorActionComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerifierReturnedToOperatorActionComponent, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      storeInitialized: true,
      action: {
        id: 1,
        requestAccountId: 13,
        submitter: 'Verifier',
        creationDate: '2022-11-29T12:12:48.469862Z',
        type: 'AER_VERIFICATION_RETURNED_TO_OPERATOR',
        payload: {
          payloadType: 'AER_VERIFICATION_RETURNED_TO_OPERATOR_PAYLOAD',
          changesRequired: 'Test change',
        },
      },
    } as CommonActionsState);

    fixture = TestBed.createComponent(VerifierReturnedToOperatorActionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryListValues).toHaveLength(1);
    expect(page.summaryListValues).toEqual([['Changes required by the operator', 'Test change']]);
  });
});
