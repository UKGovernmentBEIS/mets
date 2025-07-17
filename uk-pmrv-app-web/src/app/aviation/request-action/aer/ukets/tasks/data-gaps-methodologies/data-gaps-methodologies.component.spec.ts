import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerUkEtsRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import DataGapsMethodologiesComponent from './data-gaps-methodologies.component';

class Page extends BasePage<DataGapsMethodologiesComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('DataGapsMethodologiesComponent', () => {
  let fixture: ComponentFixture<DataGapsMethodologiesComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, DataGapsMethodologiesComponent],
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
            dataGapsMethodologies: {
              methodRequired: true,
              methodApproved: false,
              methodConservative: false,
              noConservativeMethodDetails: '111',
              materialMisstatementExist: true,
              materialMisstatementDetails: '222',
            },
          },
        } as AerUkEtsRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(DataGapsMethodologiesComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Methodologies to close data gaps');

    expect(page.summaryValues).toEqual([
      ['Was a data gap method required during the reporting year?', 'Yes'],
      ['Has the data gap method already been approved by the regulator?', 'No'],
      ['Was the method used conservative?', 'No  111'],
      ['Did the method lead to a material misstatement?', 'Yes  222'],
    ]);
  });
});
