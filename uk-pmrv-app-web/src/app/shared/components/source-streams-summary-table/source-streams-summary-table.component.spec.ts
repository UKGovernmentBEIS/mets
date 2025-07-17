import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { SharedModule } from '@shared/shared.module';

import { SourceStream } from 'pmrv-api';

import { SourceStreamsSummaryTableComponent } from './source-streams-summary-table.component';

describe('SourceStreamsSummaryTableComponent', () => {
  let component: SourceStreamsSummaryTableComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <app-source-streams-summary-table [data]="data"></app-source-streams-summary-table>
    `,
  })
  class TestComponent {
    data: SourceStream[] = [
      {
        id: '111',
        reference: 'F1',
        description: 'FUEL_GAS',
        type: 'COMBUSTION_FLARES',
      },
      {
        id: '112',
        reference: 'F2',
        description: 'COKE_OVEN_COKE_LIGNITE_COKE',
        type: 'COKE_OXIDE_OUTPUT_METHOD_B',
      },
    ];
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TestComponent],
      imports: [SharedModule],
    }).compileComponents();

    fixture = TestBed.createComponent(TestComponent);
    element = fixture.nativeElement;
    component = fixture.debugElement.query(By.directive(SourceStreamsSummaryTableComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(
      Array.from(element.querySelectorAll<HTMLTableRowElement>('tbody tr')).map((tr) =>
        Array.from(tr.querySelectorAll('td')).map((td) => td.textContent.trim()),
      ),
    ).toEqual([
      ['F1', 'Fuel Gas', 'Combustion: Flares'],
      ['F2', 'Coke Oven Coke & Lignite Coke', 'Coke: Oxide output (Method B)'],
    ]);
  });
});
