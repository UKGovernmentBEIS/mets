import { MarkedOptions } from 'marked';
import { MarkedRenderer } from 'ngx-markdown';

export function markedOptionsFactory(): MarkedOptions {
  const renderer = new MarkedRenderer();

  renderer.heading = (text: string, level: 1 | 2 | 3 | 4 | 5 | 6, raw: string): string => {
    switch (level) {
      case 1:
        return `<h1 class="govuk-heading-xl">${text}</h1>`;
      case 2:
        return `<h2 class="govuk-heading-l">${text}</h2>`;
      case 3:
        return `<h3 class="govuk-heading-m">${text}</h3>`;
      default:
        return MarkedRenderer.prototype.heading(text, level, raw);
    }
  };

  renderer.link = (href: string | null, title: string | null, text: string): string => {
    return `<a href="${href}" routerLink="${href || ''}" govukLink>${text}</a>`;
  };

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  renderer.list = (body: string, ordered: boolean, start: number): string => {
    return ordered
      ? `<ol class="govuk-list govuk-list--number">${body}</ol>`
      : `<ul class="govuk-list govuk-list--bullet">${body}</ul>`;
  };

  renderer.paragraph = (text: string) => {
    return `<p class="govuk-body">${text}</p>`;
  };

  return { renderer: renderer };
}
