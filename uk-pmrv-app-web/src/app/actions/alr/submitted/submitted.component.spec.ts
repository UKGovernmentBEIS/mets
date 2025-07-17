import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { BasePage } from '@testing';

import { alrSubmittedRequestActionPayload } from '../testing/mock-alr-submitted';
import { AlrSubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: AlrSubmittedComponent;
  let fixture: ComponentFixture<AlrSubmittedComponent>;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<AlrSubmittedComponent> {
    get heading(): string {
      return this.query('h1').textContent.trim();
    }

    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('.app-task-list__item > .app-task-list__task-name'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlrSubmittedComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_SENT_TO_VERIFIER',
        submitter: '123',
        payload: alrSubmittedRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(AlrSubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show task list', () => {
    expect(page.heading).toEqual('Activity level report submitted to verifier');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual(['Activity level report']);
  });

  it('should show task list when verifier submit alr', () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'ALR_APPLICATION_VERIFICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...alrSubmittedRequestActionPayload,
          verificationReport: {
            opinionStatement: { opinionStatementFiles: ['32882ee4-4b22-4411-bea9-ebdc9662f5d5'], notes: 'Notes' },
          } as any,
        },
      },
    });

    fixture.detectChanges();

    expect(page.heading).toEqual('Activity level report submitted to operator');
    expect(page.sections.map((el) => el.textContent.trim())).toEqual([
      'Activity level report',
      'ALR verification opinion statement',
      'Overall decision',
    ]);
  });
});
