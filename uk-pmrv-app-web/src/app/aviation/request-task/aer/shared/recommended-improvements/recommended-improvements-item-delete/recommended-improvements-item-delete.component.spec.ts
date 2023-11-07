import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import produce from 'immer';

import { RecommendedImprovementsFormProvider } from '../recommended-improvements-form.provider';
import { RecommendedImprovementsItemDeleteComponent } from './recommended-improvements-item-delete.component';

describe('RecommendedImprovementsItemDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: RecommendedImprovementsItemDeleteComponent;
  let fixture: ComponentFixture<RecommendedImprovementsItemDeleteComponent>;

  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<RecommendedImprovementsItemDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecommendedImprovementsItemDeleteComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: RecommendedImprovementsFormProvider },
        { provide: TYPE_AWARE_STORE, useExisting: RequestTaskStore },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestTaskStore);
    store.setState(
      produce(store.getState(), (state) => {
        state.isEditable = true;
        state.requestTaskItem = {
          requestTask: {
            type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
            payload: {
              verificationReport: {
                recommendedImprovements: {
                  exist: true,
                  recommendedImprovements: [{ reference: 'reference', explanation: 'explanation' }],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(RecommendedImprovementsItemDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the item name', () => {
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete ‘D1’?`);
  });

  it('should delete and navigate to list', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const saveAerVerifySpy = jest.spyOn(store.aerVerifyDelegate, 'saveAerVerify').mockReturnValue(of({}));

    page.submitButton.click();
    fixture.detectChanges();

    expect(saveAerVerifySpy).toHaveBeenCalledTimes(1);

    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: activatedRoute, queryParams: { change: true } });
  });
});
