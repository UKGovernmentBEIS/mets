import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { Router } from '@angular/router';

import htmldiff from 'html-diff';
import stripHtmlComments from 'strip-html-comments';

@Component({
  selector: 'app-highlight-diff',
  templateUrl: './highlight-diff.component.html',
  styleUrl: './highlight-diff.component.scss',
  // eslint-disable-next-line @angular-eslint/use-component-view-encapsulation
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HighlightDiffComponent implements AfterViewInit {
  @ViewChild('previous') previous: ElementRef<HTMLDivElement>;
  @ViewChild('current') current: ElementRef<HTMLDivElement>;

  diff: SafeHtml;

  constructor(
    private sanitizer: DomSanitizer,
    private cdr: ChangeDetectorRef,
    private router: Router,
  ) {}

  ngAfterViewInit(): void {
    const previous = stripHtmlComments(this.previous.nativeElement.innerHTML);
    const current = stripHtmlComments(this.current.nativeElement.innerHTML);
    const diff = htmldiff(previous, current);
    const parsedDiff = new DOMParser().parseFromString(diff, 'text/html');
    const changeLinks = Array.from(parsedDiff.querySelectorAll('a > ins.diffmod, a > del.diffmod')).filter((diffItem) =>
      ['Change', 'Remove'].includes(diffItem.textContent),
    );
    const nodeParents: ParentNode[] = [];
    changeLinks.forEach((diffItem) => {
      if (diffItem.tagName !== 'DEL') {
        nodeParents.push(diffItem.parentNode);
      }
      diffItem.parentNode.removeChild(diffItem);
    });
    nodeParents.forEach((np) => (np.textContent = 'Change'));

    this.diff = this.sanitizer.bypassSecurityTrustHtml(parsedDiff.documentElement.innerHTML);

    this.cdr.detectChanges();
  }

  onClickDiff(event: MouseEvent | KeyboardEvent) {
    let eventTarget;
    if (event.target instanceof HTMLAnchorElement) {
      eventTarget = event.target;
    } else if ((event.target as Node).nodeName === 'INS' || (event.target as Node).nodeName === 'DEL') {
      eventTarget = (event.target as Node).parentElement;
    }

    if (
      !!eventTarget &&
      eventTarget instanceof HTMLAnchorElement &&
      eventTarget.getAttribute('target') !== '_blank' &&
      event.type === 'click'
    ) {
      event.preventDefault();
      const link = eventTarget.href.replace(eventTarget.baseURI, '');
      this.router.navigateByUrl(this.router.serializeUrl(this.router.parseUrl(link)));
    }
  }
}
