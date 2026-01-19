import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { wasteQdrSubmittedRequestActionPayload } from '../testing/mock-waste-qdr-submitted';
import { WasteQdrSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: WasteQdrSubmittedComponent;
  let fixture: ComponentFixture<WasteQdrSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<WasteQdrSubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLDivElement[] {
      return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WasteQdrSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'WASTE_QDR_APPLICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...wasteQdrSubmittedRequestActionPayload,
          payloadType: 'WASTE_QDR_APPLICATION_SUBMITTED',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(WasteQdrSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show data list with details', () => {
    expect(page.heading).toEqual('Quarterly data report submitted to regulator');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Will you provide a quarterly data report for  undefined Yes',
      'Completed quarterly report',
      'Supporting data',
      'Notes Some notes',
    ]);
  });
});
