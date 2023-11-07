import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { BatchReissuesResponseDTO } from 'pmrv-api';

import { BatchReissueRequestsComponent } from './batch-reissue-requests.component';
import { mockPermitBatchReissues } from './testing/mock-data';

describe('BatchReissueRequestsComponent', () => {
  let component: BatchReissueRequestsComponent;
  let fixture;
  let hostElement: HTMLElement;
  let page: Page;

  class Page extends BasePage<any> {
    get startButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }

    get rowsCells() {
      return this.queryAll<HTMLTableRowElement>('table tr')
        .filter((row) => !row.querySelector('th'))
        .map((row) => Array.from(row.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  describe('for non empty results with permission to start batch reissue', () => {
    @Component({
      template: `
        <app-batch-reissue-requests
          [batchReissuesResponse$]="response$"
          [pageSize]="pageSize"
          (currentPageChanged)="onCurrentPageChanged($event)"
        >
        </app-batch-reissue-requests>
      `,
    })
    class TestComponent {
      response$ = of(mockPermitBatchReissues);
      pageSize = 30;
      /* eslint-disable @typescript-eslint/no-empty-function */
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      onCurrentPageChanged(page: number) {}
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [TestComponent],
        providers: [DestroySubject],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(TestComponent);
      hostElement = fixture.nativeElement;
      page = new Page(fixture);
      component = fixture.debugElement.query(By.directive(BatchReissueRequestsComponent)).componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the results', () => {
      expect(page.rowsCells).toEqual([
        ['requestId1', 'submitter_full_name1', '25 May 2023', '2'],
        ['requestId2', 'submitter_full_name2', '', ''],
      ]);
    });

    it('should display start button', () => {
      expect(page.startButton).toBeTruthy();
    });
  });

  describe('for empty results with no permission to start batch reissue', () => {
    @Component({
      template: `
        <app-batch-reissue-requests
          [batchReissuesResponse$]="response$"
          [pageSize]="pageSize"
          (currentPageChanged)="onCurrentPageChanged($event)"
        >
        </app-batch-reissue-requests>
      `,
    })
    class Test2Component {
      response$ = of({
        canInitiateBatchReissue: false,
        requestDetails: [],
        total: 0,
      } as BatchReissuesResponseDTO);
      pageSize = 30;
      /* eslint-disable @typescript-eslint/no-empty-function */
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      onCurrentPageChanged(page: number) {}
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule],
        declarations: [Test2Component],
        providers: [DestroySubject],
      }).compileComponents();
    });

    beforeEach(() => {
      fixture = TestBed.createComponent(Test2Component);
      hostElement = fixture.nativeElement;
      page = new Page(fixture);
      component = fixture.debugElement.query(By.directive(BatchReissueRequestsComponent)).componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render no results found', () => {
      expect(hostElement.textContent).toContain('There are no results to show');
    });

    it('should not display start button', () => {
      expect(page.startButton).toBeNull();
    });
  });
});
