// Browser tools — fetch pages, extract readable content, and proxy HTML for live preview.

export type BrowseResult = { text: string; title: string; url: string };

export async function browseUrl(url: string): Promise<BrowseResult> {
  const res = await fetch(url, {
    headers: { 'User-Agent': 'Mozilla/5.0 (VirgoYT-Cloud-AI/1.0)' },
    redirect: 'follow',
  });
  const html = await res.text();

  const title = extractTitle(html);
  const text = htmlToText(html);

  return { text: text || html.slice(0, 6000), title, url };
}

// Proxy endpoint handler that returns cleaned HTML for the web UI to iframe.
export async function proxyHtml(url: string): Promise<string> {
  const res = await fetch(url, {
    headers: { 'User-Agent': 'Mozilla/5.0 (VirgoYT-Cloud-AI/1.0)' },
    redirect: 'follow',
  });
  const html = await res.text();
  // Inject a base tag so relative resources resolve against the origin
  const base = `<base href="${res.url.split('/').slice(0, 3).join('/')}/">`;
  return html.replace(/<head[^>]*>/, (m) => `${m}${base}`).slice(0, 500000);
}

// Not implemented without a headless browser; preview handled via iframe proxy.
export async function screenshotUrl(_url: string): Promise<null> {
  return null;
}

function extractTitle(html: string): string {
  const m = html.match(/<title[^>]*>([^<]*)<\/title>/i);
  return m ? m[1].trim() : '';
}

function htmlToText(html: string): string {
  return html
    .replace(/<script[\s\S]*?<\/script>/gi, ' ')
    .replace(/<style[\s\S]*?<\/style>/gi, ' ')
    .replace(/<noscript[\s\S]*?<\/noscript>/gi, ' ')
    .replace(/<[^>]+>/g, ' ')
    .replace(/&nbsp;/gi, ' ')
    .replace(/&amp;/gi, '&')
    .replace(/&lt;/gi, '<')
    .replace(/&gt;/gi, '>')
    .replace(/\s+/g, ' ')
    .trim();
}