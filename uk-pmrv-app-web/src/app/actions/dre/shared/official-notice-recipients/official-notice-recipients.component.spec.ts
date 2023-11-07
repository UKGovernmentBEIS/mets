import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';

import { CommonActionsStore } from '../../../store/common-actions.store';
import { DreModule } from '../../dre.module';
import { mockState } from '../../testing/mock-dre-submitted';
import { OfficialNoticeRecipientsComponent } from './official-notice-recipients.component';

describe('OfficialNoticeRecipientsComponent', () => {
  let component: OfficialNoticeRecipientsComponent;
  let fixture: ComponentFixture<OfficialNoticeRecipientsComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<OfficialNoticeRecipientsComponent> {
    get summaryList() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const route = new ActivatedRouteStub({ actionId: 1 }, null, null);

  const createComponent = () => {
    fixture = TestBed.createComponent(OfficialNoticeRecipientsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.action = {
      payload: {
        ...mockState.action.payload,
      },
    };
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, DreModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render Official notice recipients', () => {
    createComponent();
    fixture.detectChanges();
    expect(page.summaryList).toEqual([
      ['Users', 'John Doe, Operator admin - Primary contact, Financial contact, Service contact'],
      ['Name and signature on the official notice', 'Regulator England'],
      ['Official notice', 'off notice.pdf'],
    ]);
  });
});
