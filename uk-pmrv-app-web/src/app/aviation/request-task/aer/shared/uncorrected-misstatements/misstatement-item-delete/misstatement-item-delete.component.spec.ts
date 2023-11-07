import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { ActivatedRouteStub, BasePage } from '@testing';
import produce from 'immer';

import { UncorrectedMisstatementsFormProvider } from '../uncorrected-misstatements-form.provider';
import { MisstatementItemDeleteComponent } from './misstatement-item-delete.component';

describe('MisstatementItemDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: RequestTaskStore;
  let activatedRoute: ActivatedRoute;
  let component: MisstatementItemDeleteComponent;
  let fixture: ComponentFixture<MisstatementItemDeleteComponent>;

  const route = new ActivatedRouteStub({ index: '0' });

  class Page extends BasePage<MisstatementItemDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MisstatementItemDeleteComponent, RouterTestingModule],
      providers: [
        { provide: TASK_FORM_PROVIDER, useClass: UncorrectedMisstatementsFormProvider },
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
                uncorrectedMisstatements: {
                  exist: true,
                  uncorrectedMisstatements: [{ reference: 'A1', explanation: 'explanation', materialEffect: true }],
                },
              },
            } as any,
          },
        };
      }),
    );
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(MisstatementItemDeleteComponent);
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
    expect(page.header.textContent.trim()).toEqual(`Are you sure you want to delete ‘A1’?`);
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
