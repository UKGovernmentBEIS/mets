import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { KeycloakService } from 'keycloak-angular';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { ActionSharedModule } from '../../shared/action-shared-module';
import { BaseActionContainerComponent } from '../../shared/components/base-task-container-component/base-action-container.component';
import { CommonActionsStore } from '../../store/common-actions.store';
import { mockCompletedActionState } from '../testing/mock-data';
import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<CompletedComponent> {
    get heading(): string {
      return this.query<HTMLHeadingElement>('h1.govuk-heading-l').textContent.trim();
    }
    get values() {
      return this.queryAll<HTMLElement>(
        'dl.govuk-summary-list > div.govuk-summary-list__row >  dd.govuk-summary-list__value',
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, ActionSharedModule],
      providers: [KeycloakService],
      declarations: [CompletedComponent, BaseActionContainerComponent],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);
    store.setState(mockCompletedActionState);

    fixture = TestBed.createComponent(CompletedComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display header', () => {
    expect(page.heading).toEqual('Batch variation completed');
  });

  it('should display values', () => {
    expect(page.values.map((val) => val.textContent.trim())).toEqual([
      'submitter1',
      '8 Jun 2023',
      'signName',
      'BRI0048-E.csv',
      'Live',
      'GHGE',
      'A (low emitter)',
      '10',
    ]);
  });
});
