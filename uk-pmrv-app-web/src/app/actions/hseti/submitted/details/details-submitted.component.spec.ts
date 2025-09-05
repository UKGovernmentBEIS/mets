import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { hsetiSubmittedRequestActionPayload } from '@actions/hseti/testing/mock-hseti-submitted';
import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { HsetiDetailsSubmittedComponent } from './details-submitted.component';

describe('DetailsSubmittedComponent', () => {
  let component: HsetiDetailsSubmittedComponent;
  let fixture: ComponentFixture<HsetiDetailsSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<HsetiDetailsSubmittedComponent> {
    get detailsSummaryTemplate() {
      return this.query('app-hseti-details-summary-template');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HsetiDetailsSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'HSE_TI_APPLICATION_SENT_TO_REGULATOR',
        submitter: '123',
        payload: {
          ...hsetiSubmittedRequestActionPayload,
          payloadType: 'HSETI_APPLICATION_SUBMITTED_REQUEST',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(HsetiDetailsSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display summary HTMLElement', () => {
    expect(page.detailsSummaryTemplate).toBeTruthy();
  });
});
