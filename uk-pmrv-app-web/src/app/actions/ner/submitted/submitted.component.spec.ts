import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { nerSubmittedRequestActionPayload } from '../testing';
import { NerSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: NerSubmittedComponent;
  let fixture: ComponentFixture<NerSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<NerSubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NerSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'NER_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: nerSubmittedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(NerSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.heading).toEqual('Submitted to verifier');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['New entrant reserve']);
  });
});
