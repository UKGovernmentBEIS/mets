import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { BasePage, RouterStubComponent } from '@testing';

import { BulkDownloadsComponent } from './bulk-downloads.component';
import { BulkDownloadsService } from './core/bulk-downloads.service';

describe('BulkDownloadsComponent', () => {
  let fixture: ComponentFixture<BulkDownloadsComponent>;
  let component: BulkDownloadsComponent;
  let page: Page;

  const workflows = ['ALR'];

  const mockBulkDownloadsService = {
    getBulkDownloadWorkflows$: of(workflows),
  };

  class Page extends BasePage<BulkDownloadsComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('table tbody tr');
    }
    get rowTexts() {
      return this.rows.map((r) => r.textContent?.trim() ?? '');
    }
    get links() {
      return this.queryAll<HTMLAnchorElement>('a').map((a) => a.getAttribute('href'));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, BulkDownloadsComponent],
      providers: [
        provideRouter([{ path: 'bulk-downloads', component: RouterStubComponent }]),
        { provide: BulkDownloadsService, useValue: mockBulkDownloadsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BulkDownloadsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('maps workflows to name and link', (done) => {
    component.enabledWorkflows$.subscribe((items) => {
      expect(items).toEqual([{ name: 'ALR', link: '../bulk-downloads/alr' }]);
      done();
    });
  });

  it('renders workflows and links in the view', async () => {
    fixture.detectChanges();
    await fixture.whenStable();

    expect(page.rowTexts[0]).toEqual('Activity Level Report');

    expect(page.links).toEqual(['/bulk-downloads/alr']);
  });
});
