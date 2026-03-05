import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BatchReissueCompletedRequestActionPayload, BatchReissueSubmittedRequestActionPayload } from 'pmrv-api';

import { BasePage } from '../../../../../testing';
import { mockSubmittedActionState } from '../../../../actions/permit-batch-reissue/testing/mock-data';
import { SharedModule } from '../../../shared.module';
import { FiltersModel } from '../filters.model';
import { FiltersTemplateComponent } from './filters-template.component';

describe('FiltersTemplateComponent', () => {
  let component: TestWrapperComponent;
  let fixture: ComponentFixture<TestWrapperComponent>;
  let page: Page;

  class Page extends BasePage<TestWrapperComponent> {
    get values() {
      return this.queryAll<HTMLElement>('dl > div >  dd');
    }
  }

  @Component({
    selector: 'app-test-wrapper-component',
    template: `
      <app-permit-batch-reissue-filters-template
        [filters]="filters"
        [editable]="editable"></app-permit-batch-reissue-filters-template>
    `,
  })
  class TestWrapperComponent {
    filters: FiltersModel = {
      ...((mockSubmittedActionState.action.payload as BatchReissueSubmittedRequestActionPayload)
        .filters as FiltersModel),
      numberOfEmitters: null,
    };
    editable = false;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestWrapperComponent, FiltersTemplateComponent],
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestWrapperComponent);
    page = new Page(fixture);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display submitted action values', () => {
    expect(page.values.map((val) => val.textContent.trim())).toEqual(['Live', 'GHGE', 'A (low emitter)']);
  });

  it('should display edit button', () => {
    component.editable = true;
    fixture.detectChanges();
    expect(page.values.map((val) => val.textContent.trim())).toEqual([
      'Live',
      'Change',
      'GHGE',
      'Change',
      'A (low emitter)',
      'Change',
    ]);
  });

  it('should display completed action values', () => {
    component.filters = {
      ...((mockSubmittedActionState.action.payload as BatchReissueCompletedRequestActionPayload)
        .filters as FiltersModel),
      numberOfEmitters: 10,
    };
    fixture.detectChanges();
    expect(page.values.map((val) => val.textContent.trim())).toEqual(['Live', 'GHGE', 'A (low emitter)', '10']);
  });
});
