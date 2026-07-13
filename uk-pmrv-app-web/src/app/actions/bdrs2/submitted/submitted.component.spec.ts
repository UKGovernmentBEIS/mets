import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { bdrs2SubmittedRequestActionPayload } from '../testing/mock-bdrs2-submitted';
import { Bdrs2SubmittedComponent } from './submitted.component';

describe('Bdrs2SubmittedComponent', () => {
  let component: Bdrs2SubmittedComponent;
  let fixture: ComponentFixture<Bdrs2SubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<Bdrs2SubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Bdrs2SubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'BDRS2_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: {
          ...bdrs2SubmittedRequestActionPayload,
          payloadType: 'BDRS2_APPLICATION_SENT_TO_VERIFIER_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(Bdrs2SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.heading).toEqual('Stage 2 baseline data report submitted to verifier');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Provide stage 2 baseline data report']);
  });
});
