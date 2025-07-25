import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../../../../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  @Component({
    template: `
      <app-deem-withdraw-determination-summary-details
        [deemWithdrawDetermination$]="deemWithdrawDetermination$"></app-deem-withdraw-determination-summary-details>
    `,
  })
  class TestComponent {
    deemWithdrawDetermination$ = store.select('reviewDetermination');
    isEditable$ = store.select('isEditable');
  }

  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<TestComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLinks() {
      return this.queryAll<HTMLAnchorElement>('a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryDetailsComponent)).componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryDetailsComponent, TestComponent],
      imports: [RouterTestingModule, SharedPermitSurrenderModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  it('should create', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render deem summary', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.rows).toEqual([
      ['Decision', 'Deem withdrawn'],
      ['Reason for decision', 'deemed reason'],
    ]);

    page.changeLinks[0].click();
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route, state: { changing: true } });

    page.changeLinks[1].click();
    expect(navigateSpy).toHaveBeenCalledWith(['../reason'], { relativeTo: route, state: { changing: true } });
  });
});
