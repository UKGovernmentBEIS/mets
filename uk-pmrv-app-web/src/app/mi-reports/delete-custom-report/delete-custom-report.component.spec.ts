import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import { MiReportsUserDefinedService } from 'pmrv-api';

import { DeleteCustomReportComponent } from './delete-custom-report.component';

describe('DeleteCustomReportComponent', () => {
  let component: DeleteCustomReportComponent;
  let fixture: ComponentFixture<DeleteCustomReportComponent>;
  let page: Page;
  let router: Router;
  let miReportsService: Partial<jest.Mocked<MiReportsUserDefinedService>>;

  class Page extends BasePage<DeleteCustomReportComponent> {
    get heading(): HTMLElement {
      return this.query('app-page-heading');
    }

    get body(): HTMLParagraphElement {
      return this.query('p.govuk-body');
    }

    get deleteButton(): HTMLButtonElement {
      return this.query('button');
    }

    get cancelLink(): HTMLAnchorElement {
      return this.query('a');
    }
  }

  const routeStub = new ActivatedRouteStub({ id: '7' });

  beforeEach(async () => {
    miReportsService = {
      deleteCustomReport: jest.fn().mockReturnValue(of({})),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: routeStub },
        { provide: MiReportsUserDefinedService, useValue: miReportsService },
      ],
      declarations: [DeleteCustomReportComponent],
    }).compileComponents();

    router = TestBed.inject(Router);
    jest.spyOn(router, 'navigate').mockResolvedValue(true);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteCustomReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the confirmation message with a delete button and a cancel link', () => {
    expect(page.heading.textContent).toContain('Are you sure you want to delete this report?');
    expect(page.body.textContent.trim()).toBe('This report will no longer be available to you or other METS users.');
    expect(page.deleteButton.textContent.trim()).toBe('Yes, delete');
    expect(page.cancelLink.textContent.trim()).toBe('Cancel');
  });

  it('should delete the report and navigate back to the list with a success notification', () => {
    page.deleteButton.click();
    fixture.detectChanges();

    expect(miReportsService.deleteCustomReport).toHaveBeenCalledWith(7);
    expect(router.navigate).toHaveBeenCalledWith(['../../..'], {
      relativeTo: routeStub,
      state: { notification: 'The report has been deleted' },
    });
  });
});
