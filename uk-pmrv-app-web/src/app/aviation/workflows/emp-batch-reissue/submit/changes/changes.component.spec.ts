import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { EmpBatchReissueStore } from '../store/emp-batch-reissue.store';
import { ChangesComponent } from './changes.component';

describe('ChangesComponent', () => {
  let component: ChangesComponent;
  let fixture: ComponentFixture<ChangesComponent>;
  let page: Page;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let store: EmpBatchReissueStore;

  const route = new ActivatedRouteStub();

  class Page extends BasePage<ChangesComponent> {
    get header() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get changes() {
      return this.queryAll<HTMLTextAreaElement>('textarea');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll('li')).map((item) => item.textContent.trim());
    }

    get removeButtons(): HTMLButtonElement[] {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).filter(
        (button) => button.textContent.trim() === 'Remove',
      );
    }

    get addItemButton(): HTMLButtonElement[] {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).filter(
        (button) => button.textContent.trim() === 'Add another item',
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangesComponent, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(EmpBatchReissueStore);
    store.setState({
      reportingStatuses: ['REQUIRED_TO_REPORT', 'EXEMPT_COMMERCIAL'],
      emissionTradingSchemes: ['UK_ETS_AVIATION'],
      signatory: undefined,
      changesDetails: {
        changesSummary: 'summary of changes',
        changes: [],
      },
    });

    fixture = TestBed.createComponent(ChangesComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    activatedRoute = TestBed.inject(ActivatedRoute);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the correct header and change text', () => {
    expect(page.header.textContent.trim()).toEqual(
      'Start a batch variationList changes to include in the variation schedule',
    );
    expect(page.changes[0].value).toEqual('');
  });

  it('should show validation errors', () => {
    page.changes[0].value = '';
    page.changes[0].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual(['Enter a change to include in the variation schedule']);
  });

  it('should update the state', () => {
    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: [],
    });

    page.changes[0].value = 'new updated summary of changes';
    page.changes[0].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: ['new updated summary of changes'],
    });
  });

  it('should add and remove changes', () => {
    expect(page.changes.length).toEqual(1);
    expect(page.removeButtons.length).toEqual(0);
    expect(page.addItemButton).toBeTruthy();

    // add 2 changes
    page.addItemButton[0].click();
    fixture.detectChanges();
    page.addItemButton[0].click();
    fixture.detectChanges();
    expect(page.changes.length).toEqual(3);
    expect(page.removeButtons.length).toEqual(3);
    expect(page.changes[1].value).toEqual('');
    expect(page.changes[2].value).toEqual('');

    // remove the 2nd change
    page.removeButtons[0].click();
    fixture.detectChanges();

    expect(page.changes.length).toEqual(2);
    expect(page.removeButtons.length).toEqual(2);
    expect(page.changes[0].value).toEqual('');
    expect(page.changes[1].value).toEqual('');
  });

  it('should submit at least one change', () => {
    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: [],
    });
    page.addItemButton[0].click();
    fixture.detectChanges();

    page.changes[0].value = '';
    page.changes[0].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual(['Enter a change to include in the variation schedule']);
    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: [],
    });

    page.changes[0].value = 'first change';
    page.changes[0].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: ['first change', ''],
    });
  });

  it('should navigate to the next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.changes[0].value = 'new updated summary of changes';
    page.changes[0].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
      changes: ['new updated summary of changes'],
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'signatory'], {
      relativeTo: activatedRoute,
    });
  });
});
