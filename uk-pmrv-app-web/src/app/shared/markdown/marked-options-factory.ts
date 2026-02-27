import { MarkedOptions, Tokens } from 'marked';
import { MarkedRenderer } from 'ngx-markdown';

export function markedOptionsFactory(): MarkedOptions {
  const renderer = new MarkedRenderer();

  renderer.heading = ({ tokens, depth, type, raw, text }): string => {
    switch (depth) {
      case 1:
        return `<h1 class="govuk-heading-xl">${text}</h1>`;
      case 2:
        return `<h2 class="govuk-heading-l">${text}</h2>`;
      case 3:
        return `<h3 class="govuk-heading-m">${text}</h3>`;
      default:
        return MarkedRenderer.prototype.heading({ tokens, depth, type, raw, text });
    }
  };

  renderer.list = (token): string => {
    const list = token.items.map((item) => `<li>${item.text}</li>`).reduce((prev, curr) => `${prev}${curr}`, '');

    if (token.ordered) {
      return `<ol class="govuk-list govuk-list--number">${list}</ol>`;
    } else {
      return `<ul class="govuk-list govuk-list--bullet">${list}</ul>`;
    }
  };

  renderer.paragraph = ({ tokens }) => {
    return tokens
      .map((el) => {
        const { text, href } = el as Tokens.Paragraph & Tokens.Link;

        if (el.type === 'text') {
          return text;
        } else if (el.type === 'link') {
          return `<a href="${href}" routerLink="${href || ''}" govukLink>${text}</a>`;
        }
      })
      .reduce((prev, curr, i) => `${prev}${i === tokens.length - 1 ? curr + '</p>' : curr}`, '<p class="govuk-body">');
  };

  return { renderer };
}
