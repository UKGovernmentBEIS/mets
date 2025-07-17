import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RequestActionStore } from '@aviation/request-action/store';
import { BasePage } from '@testing';

import { AviationAerVerifierReturnedComponent } from './verifier-returned.component';

describe('AviationAerVerifierReturnedComponent', () => {
  let component: AviationAerVerifierReturnedComponent;
  let fixture: ComponentFixture<AviationAerVerifierReturnedComponent>;
  let store: RequestActionStore;
  let page: Page;

  class Page extends BasePage<AviationAerVerifierReturnedComponent> {
    get header(): string {
      return this.query('app-page-heading').textContent.trim();
    }
    get summaryValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AviationAerVerifierReturnedComponent, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        id: 1,
        type: 'AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR',
        creationDate: '2024-10-15T12:12:48.469862Z',
        payload: {
          payloadType: 'AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR_PAYLOAD',
          changesRequired: 'changes',
        } as any,
      },
      regulatorViewer: true,
    });

    fixture = TestBed.createComponent(AviationAerVerifierReturnedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Verifier returned to operator for changes');
    expect(page.summaryValues).toEqual([['Changes required by the operator', 'changes']]);
  });
});
