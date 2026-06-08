import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { ActivatedRouteStub, BasePage } from '@testing';

import { VerificationReturnedToOperatorComponent } from './verification-returned-to-operator.component';

describe('VerificationReturnedToOperatorComponent', () => {
  let component: VerificationReturnedToOperatorComponent;
  let fixture: ComponentFixture<VerificationReturnedToOperatorComponent>;
  let page: Page;
  let store: CommonActionsStore;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      input: { changesRequired: 'Changes', actionType: 'ALR_VERIFICATION_RETURNED_TO_OPERATOR' },
    },
  );

  class Page extends BasePage<VerificationReturnedToOperatorComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get summaryContents() {
      return this.queryAll('h2, dl dt, dl dd').map((item) => item.textContent.trim());
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VerificationReturnedToOperatorComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_VERIFICATION_RETURNED_TO_OPERATOR',
      },
    });

    fixture = TestBed.createComponent(VerificationReturnedToOperatorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary content', () => {
    expect(page.heading).toEqual('Activity level report returned to operator for changes');
    expect(page.summaryContents).toEqual(['Changes required from operator', 'Changes']);
  });
});
