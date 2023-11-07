import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import UncorrectedNonConformitiesComponent from './uncorrected-non-conformities.component';

class Page extends BasePage<UncorrectedNonConformitiesComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('UncorrectedNonConformitiesComponent', () => {
  let fixture: ComponentFixture<UncorrectedNonConformitiesComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, UncorrectedNonConformitiesComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
        creationDate: '2023-09-20T12:18:46.714Z',
        payload: {
          verificationReport: {
            uncorrectedNonConformities: {
              existUncorrectedNonConformities: false,
              uncorrectedNonConformities: [
                { reference: 'reference', explanation: 'explanation', materialEffect: false },
              ],
              existPriorYearIssues: true,
              priorYearIssues: [{ reference: 'reference', explanation: 'explanation' }],
            },
          },
        } as AerRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(UncorrectedNonConformitiesComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Uncorrected non-conformities');

    expect(page.summaryValues).toEqual([
      ['Have there been any uncorrected non-conformities with the approved emissions monitoring plan?', 'No'],
      ['Are there any non-conformities from the previous year that have not been resolved?', 'Yes'],
    ]);
  });
});
