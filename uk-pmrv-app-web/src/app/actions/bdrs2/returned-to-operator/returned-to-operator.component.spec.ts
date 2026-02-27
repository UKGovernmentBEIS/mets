import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { BdrS2ReturnedToOperatorComponent } from './returned-to-operator.component';

describe('ReturnedToOperatorComponent', () => {
  let component: BdrS2ReturnedToOperatorComponent;
  let fixture: ComponentFixture<BdrS2ReturnedToOperatorComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<BdrS2ReturnedToOperatorComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get summaryContents() {
      return this.queryAll('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrS2ReturnedToOperatorComponent],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDRS2_VERIFICATION_RETURNED_TO_OPERATOR',
        submitter: '123',
        payload: {
          payloadType: 'BDRS2_VERIFICATION_RETURNED_TO_OPERATOR_PAYLOAD',
          changesRequired: 'Changes',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(BdrS2ReturnedToOperatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary content', () => {
    expect(page.heading).toEqual('Stage 2 baseline data report returned to operator for changes');
    expect(page.summaryContents).toEqual(['Changes required from operator', 'Changes']);
  });
});
