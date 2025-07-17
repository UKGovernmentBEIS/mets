import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { bdrSubmittedRequestActionPayload } from '../testing/mock-bdr-submitted';
import { BdrSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: BdrSubmittedComponent;
  let fixture: ComponentFixture<BdrSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<BdrSubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BdrSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDR_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: {
          ...bdrSubmittedRequestActionPayload,
          payloadType: 'BDR_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(BdrSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.heading).toEqual('Baseline data report submitted to verifier');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Baseline data report and details']);
  });
});
