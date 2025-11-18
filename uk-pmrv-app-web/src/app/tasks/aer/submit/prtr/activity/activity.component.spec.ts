import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter, Router } from '@angular/router';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage } from '@testing';

describe('ActivityComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ActivityComponent;
  let fixture: ComponentFixture<ActivityComponent>;
  let router: Router;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({
    paramMap: of(convertToParamMap({ index: 0 })),
    snapshot: {
      paramMap: convertToParamMap({ index: 0 }),
    },
  });

  class Page extends BasePage<ActivityComponent> {
    get activityRadioButtons() {
      return this.queryAll<HTMLInputElement>('input[name$="activity"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ActivityComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
      ],
    }).compileComponents();
  });

  describe('for new prtr', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockStateBuild({ prtrCodes: null }));
      router = TestBed.inject(Router);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show activities and submit form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.activityRadioButtons.map((el) => el.value)).toEqual([
        '_1',
        '_2',
        '_3',
        '_4',
        '_5',
        '_6',
        '_7',
        '_8',
        '_9',
      ]);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryList).toEqual(['Enter the relevant sector']);

      page.activityRadioButtons[1].click();
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../../activity', 0, 'subActivity'], {
        relativeTo: activatedRoute,
        queryParams: { activityItem: '_2' },
      });
    });
  });
});
