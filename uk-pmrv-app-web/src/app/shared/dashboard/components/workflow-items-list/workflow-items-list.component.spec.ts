import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { ItemTypePipe } from '@shared/dashboard';
import { DaysRemainingPipe } from '@shared/pipes/days-remaining.pipe';
import { ItemLinkPipe } from '@shared/pipes/item-link.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';

import { TableComponent } from 'govuk-components';

import * as mocks from '../../testing';
import { WorkflowItemsListComponent } from './workflow-items-list.component';

/* eslint-disable @angular-eslint/component-selector */
@Component({
  selector: '',
  template: `
    <app-workflow-items-list
      [items]="items"
      [tableColumns]="tableColumns"
      [unassignedLabel]="'Unassigned'"></app-workflow-items-list>
  `,
})
class TestParentComponent {
  items = mocks.assignedItems;
  tableColumns = mocks.columns;
}

describe('WorkflowItemsListComponent', () => {
  let component: TestParentComponent;
  let fixture: ComponentFixture<TestParentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [
        TestParentComponent,
        WorkflowItemsListComponent,
        ItemLinkPipe,
        ItemNamePipe,
        ItemTypePipe,
        UserFullNamePipe,
        DaysRemainingPipe,
        TableComponent,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TestParentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show data in table', () => {
    const cells = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('td'));
    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...['Apply for a permit', 'TEST_FN TEST_LN', '22', 'PERM_REF_ID_1', 'ACCOUNT_1', 'LE_1'],
      ...['Peer review permit surrender', 'TEST_FN TEST_LN', '12', 'PERM_REF_ID_2', 'ACCOUNT_2', 'LE_2'],
      ...['Review installation account application', 'TEST_FN TEST_LN', '10', 'PERM_REF_ID_3', 'ACCOUNT_3', 'LE_3'],
      ...['Permit application sent to regulator', 'TEST_FN TEST_LN', '16', 'PERM_REF_ID_4', 'ACCOUNT_4', 'LE_4'],
    ]);
  });
});
