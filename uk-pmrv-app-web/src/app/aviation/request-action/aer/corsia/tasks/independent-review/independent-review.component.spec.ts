import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import IndependentReviewComponent from '@aviation/request-action/aer/corsia/tasks/independent-review/independent-review.component';
import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerCorsiaRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

class Page extends BasePage<IndependentReviewComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('IndependentReviewComponent', () => {
  let component: IndependentReviewComponent;
  let fixture: ComponentFixture<IndependentReviewComponent>;
  let store: RequestActionStore;
  const route = new ActivatedRouteStub();
  let page: Page;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, IndependentReviewComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
        creationDate: '2022-11-29T12:12:48.469862Z',
        payload: {
          verificationReport: {
            independentReview: {
              reviewResults: 'My review results',
              name: 'My name',
              position: 'My position',
              email: 'test@pmrv.com',
              line1: 'Korinthou 4, Neo Psychiko',
              city: 'Athens',
              country: 'GR',
            },
          },
        } as AerCorsiaRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(IndependentReviewComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Independent review');
    expect(page.summaryValues).toEqual([
      ['Results of the independent review', 'My review results'],
      ['Name', 'My name'],
      ['Position', 'My position'],
      ['Email', 'test@pmrv.com'],
      ['Address', `Korinthou 4, Neo Psychiko Athens`],
    ]);
  });
});
