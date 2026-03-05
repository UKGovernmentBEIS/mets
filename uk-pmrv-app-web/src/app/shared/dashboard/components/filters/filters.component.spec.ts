import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { DashboardFiltersComponent } from './filters.component';

describe('DashboardFiltersComponent', () => {
  let component: DashboardFiltersComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <app-dashboard-filters
        [order]="this.currentOrder"
        [filter]="this.currentFilter"
        (orderByChange)="this.currentOrder = $event"
        (filterByChange)="this.currentFilter = $event"
        [filterRequestTypes]="this.currentfilterRequestTypes"
        [searchTerm]="this.currentAccountSearchTerm"
        (searchByChange)="this.currentAccountSearchTerm = $event"></app-dashboard-filters>
    `,
  })
  class TestComponent {
    currentOrder;
    currentFilter;
    currentfilterRequestTypes;
    currentAccountSearchTerm;
  }

  class Page extends BasePage<TestComponent> {
    get orderSelect(): HTMLSelectElement {
      return this.queryAll('select')[1];
    }

    get orderOptions(): string[] {
      return Array.from(this.orderSelect.options).map((option) => option.textContent.trim());
    }

    get orderValue(): string {
      return this.orderSelect.value;
    }
    set orderValue(value: string) {
      this.setInputValue('#orderBy', value);

      const select = this.orderSelect;
      select.value = value;
      select.dispatchEvent(new Event('change'));
    }

    get filterSelect(): HTMLSelectElement {
      return this.queryAll('select')[0];
    }

    get filterOptions(): string[] {
      return Array.from(this.filterSelect.options).map((option) => option.textContent.trim());
    }

    get filterValue(): string {
      return this.filterSelect.value;
    }

    set filterValue(value: string) {
      this.setInputValue('#filterBy', value);

      const select = this.filterSelect;
      select.value = value;
      select.dispatchEvent(new Event('change'));
    }

    set searchValue(value: string) {
      this.setInputValue('[name="search"]', value);
    }

    get searchValue() {
      return this.getInputValue('[name="search"]');
    }

    get searchButton() {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule, SharedModule],
      providers: [provideRouter([])],
      declarations: [DashboardFiltersComponent, TestComponent],
    });
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(DashboardFiltersComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render sorting filters', () => {
    expect(page.orderOptions).toEqual(['Newest first', 'Days remaining']);
  });

  it('should change order value', async () => {
    hostComponent.currentOrder = 'NEAREST_DUE_DATE';
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.orderValue).toEqual('NEAREST_DUE_DATE');
  });

  it('should emit currentOrder', async () => {
    hostComponent.currentOrder = 'NEWEST_FIRST';
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.orderValue).toEqual('NEWEST_FIRST');
    fixture.detectChanges();
    await fixture.whenStable();
    page.orderValue = 'NEAREST_DUE_DATE';
    await fixture.whenStable();
    fixture.detectChanges();
    expect(hostComponent.currentOrder).toEqual('NEAREST_DUE_DATE');
  });

  it('should render filters', async () => {
    hostComponent.currentfilterRequestTypes = ['AER'];
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.filterOptions).toEqual(['All workflows', 'Emissions report']);
  });

  it('should change filter value', async () => {
    hostComponent.currentFilter = 'AER';
    hostComponent.currentfilterRequestTypes = ['AER'];
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.filterValue).toEqual('AER');
  });

  it('should emit currentFilter', async () => {
    hostComponent.currentFilter = 'AER';
    hostComponent.currentfilterRequestTypes = ['AER'];
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.filterValue).toEqual('AER');
    fixture.detectChanges();
    await fixture.whenStable();
    page.filterValue = '';
    await fixture.whenStable();
    fixture.detectChanges();
    expect(hostComponent.currentFilter).toEqual('');
  });

  it('should change search value', async () => {
    hostComponent.currentAccountSearchTerm = '98';
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.searchValue).toEqual('98');
  });

  it('should emit search term', async () => {
    page.searchValue = '97';
    page.searchButton.click();
    fixture.detectChanges();
    await fixture.whenStable();
    expect(page.searchValue).toEqual('97');
  });
});
