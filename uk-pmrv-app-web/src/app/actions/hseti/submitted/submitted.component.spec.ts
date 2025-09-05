import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { hsetiSubmittedRequestActionPayload } from '../testing/mock-hseti-submitted';
import { HseTiSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: HseTiSubmittedComponent;
  let fixture: ComponentFixture<HseTiSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<HseTiSubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HseTiSubmittedComponent],
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

    fixture = TestBed.createComponent(HseTiSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.heading).toEqual('2021-2025 HSE target increase application submitted to regulator');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Provide the HSE details']);
  });
});
