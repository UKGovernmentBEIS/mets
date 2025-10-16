import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { PermitBatchReissueStore } from '../store/permit-batch-reissue.store';
import { ChangesSummaryComponent } from './changes-summary.component';

describe('ChangesSummaryComponent', () => {
  let component: ChangesSummaryComponent;
  let fixture: ComponentFixture<ChangesSummaryComponent>;
  let page: Page;
  let activatedRoute: ActivatedRoute;
  let router: Router;
  let store: PermitBatchReissueStore;

  const route = new ActivatedRouteStub();

  class Page extends BasePage<ChangesSummaryComponent> {
    get header() {
      return this.query<HTMLElement>('app-page-heading');
    }

    get changesSummary() {
      return this.query<HTMLTextAreaElement>('textarea');
    }

    get errorSummary(): HTMLDivElement {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryListContents(): string[] {
      return Array.from(this.errorSummary.querySelectorAll<HTMLAnchorElement>('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get submitButton(): HTMLButtonElement {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangesSummaryComponent, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    store = TestBed.inject(PermitBatchReissueStore);
    store.setState({
      changesDetails: {
        changesSummary: 'summary of changes',
      },
      accountStatuses: [],
      emitterTypes: [],
      installationCategories: [],
      signatory: '',
    });

    fixture = TestBed.createComponent(ChangesSummaryComponent);
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

  it('should show the correct header and summary text', () => {
    expect(page.header.textContent.trim()).toEqual(
      'Start a batch variationEnter a summary of changes for the permit variation log',
    );
    expect(page.changesSummary.value).toEqual('summary of changes');
  });

  it('should show validation errors', () => {
    page.changesSummary.value = '';
    page.changesSummary.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryListContents).toEqual(['Enter a summary of changes for the permit variation log']);
  });

  it('should update the state', () => {
    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
    });

    page.changesSummary.value = 'new updated summary of changes';
    page.changesSummary.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'new updated summary of changes',
    });
  });

  it('should navigate to the next page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(store.getState().changesDetails).toEqual({
      changesSummary: 'summary of changes',
    });

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'changes'], {
      relativeTo: activatedRoute,
    });
  });
});
