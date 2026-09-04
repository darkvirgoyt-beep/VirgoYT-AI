// VirgoYT offline "Local Brain" — deterministic reasoning used when no AI
// provider is connected, so the site stays genuinely useful (real math,
// conversions, code, terminal help) instead of returning canned text.

const OP: Record<string, (a: number, b: number) => number> = {
  '+': (a, b) => a + b,
  '-': (a, b) => a - b,
  '*': (a, b) => a * b,
  '/': (a, b) => a / b,
  'plus': (a, b) => a + b,
  'minus': (a, b) => a - b,
  'times': (a, b) => a * b,
  'divided by': (a, b) => a / b,
};

export function localReason(prompt: string): string {
  const p = prompt.trim();
  const lower = p.toLowerCase();

  // ---- math ----
  const math = tryMath(lower);
  if (math) return math;

  // ---- conversions ----
  if (/km|kilometer/i.test(lower) && /miles?\b/i.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n} km ≈ ${(n * 0.621371).toFixed(2)} miles`;
  }
  if (/miles?\b/i.test(lower) && /km|kilometer/i.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n} miles ≈ ${(n * 1.60934).toFixed(2)} km`;
  }
  if (/fahrenheit|°f|\bf\b/i.test(lower) && /celsius|°c/.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n}°F = ${(((n - 32) * 5) / 9).toFixed(1)}°C`;
  }
  if (/celsius|°c/.test(lower) && /fahrenheit|°f/.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n}°C = ${((n * 9) / 5 + 32).toFixed(1)}°F`;
  }
  if (/kg|kilogram/.test(lower) && /pounds?|lbs?/.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n} kg ≈ ${(n * 2.20462).toFixed(2)} lb`;
  }
  if (/pounds?|lbs?/.test(lower) && /kg|kilogram/.test(lower)) {
    const n = num(lower);
    if (n !== null) return `${n} lb ≈ ${(n * 0.453592).toFixed(2)} kg`;
  }

  // ---- code snippets ----
  if (/html\b/.test(lower) && (/page|website|site|create|make/).test(lower)) {
    return htmlSnippet();
  }
  if (/python|py\b/.test(lower) && (/http|server|serve/).test(lower)) {
    return pythonServer();
  }
  if (/(react|component|app)/.test(lower) && (/create|make|component/).test(lower)) {
    return reactComponent();
  }

  // ---- simple reasoning ----
  if (/(what is|who is|tell me about)\s+(virgoyt|virgo)/.test(lower)) {
    return `**VirgoYT Cloud AI** is an autonomous cloud software engineer that runs right in your browser. It gives you:
• A real Linux **terminal** sandbox
• **Code editor** with live tabs
• **File manager**
• **Browser sandbox** to test sites
• A **multi-agent workforce** (architect → coder → security → devops)
• **Factory mode** to plan, build and ship full apps
• **Build & Deploy** + **cyber-defense** scanning

Tell me what you want to build and I'll use these tools to do it.`;
  }
  if (/(how do you work|how does|what can you do|features?)/.test(lower)) {
    return `I can **actually do** things in your cloud workspace — not just chat:
• **Create/run files** — tell me and I'll write code into your editor
• **Run terminal commands** — in a real Linux sandbox
• **Browse & test** websites live
• **Build a full app** via Factory/Workforce mode
• **Export & deploy** as web / exe / apk / mac
• **Scan for security** issues in your own code

Since no API key is connected yet, I'm answering from my built-in offline brain — connect a model and I'll reason live with your full context.`;
  }
  if (/(hello|hi|hey|yo)\b/.test(lower)) {
    return `Hello! 👋 I'm your **VirgoYT AI**. I can build, code, run commands and deploy in your cloud workspace.

What would you like to make today? (e.g. "create a todo web app", "run npm start", "write a Python server")`;
  }
  if (/(thank|thanks|great|awesome)/.test(lower)) {
    return `You're welcome! 😊 Let me know what you'd like to build next — I'm here to actually do the work, not just talk.`;
  }
  if (/^(create|make|build|write)[\s\S]*/.test(lower)) {
    return `I'm ready to **build that** in your workspace. Since no AI provider is connected yet, I can't run the full agent loop live — connect a backend (Google Script URL or an API key) and I'll create, run and test it end-to-end.

Meanwhile, here's the plan I'd follow:
1. **Plan** — break it into steps
2. **Scaffold** — create the files in your workspace
3. **Code** — write the implementation
4. **Run/Test** — execute in the terminal sandbox
5. **Deploy** — export & publish

Plug in a model and I'll execute all 5 steps.`;
  }
  if (/(error|bug|not working|fix)/.test(lower)) {
    return `Let's debug it together. 🔍

Could you paste the **exact error message** from the terminal? With that I can:
1. Identify the root cause
2. Propose a fix
3. Apply it to your workspace file

When a backend model is connected I'll also read your actual project files for context, not just guess.`;
  }

  // ---- terminal & general help ----
  if (/(terminal|command|shell|run)/.test(lower)) {
    return `Here's a quick terminal reference for your sandbox:
\`\`\`bash
# run a script
node app.js

# install a package
npm install express

# start a server
npm start
\`\`\`

Type the task ("set up a React app", "install axios") and I'll run it in the terminal panel.`;
  }

  return `Here's what I can help with using my built-in brain (no external model connected yet):

• **Math / conversions** — "what is 15 * 23?" / "10 km in miles"
• **Code** — "create an HTML page", "Python HTTP server", "React component"
• **Terminal** — "how do I npm install"
• **Build** — "build me a todo app"

Or tell me about **Virgo** to see everything I can do. Once you connect a backend model, I'll reason with your full workspace context and execute tasks live.`;
}

function tryMath(lower: string): string | null {
  const m = lower.match(/(\d+\.?\d*)\s*(plus|minus|times|divided by|\+|-|\*|\/|x)\s*(\d+\.?\d*)/);
  if (!m) return null;
  const a = parseFloat(m[1]);
  const b = parseFloat(m[3]);
  const op = m[2] === 'x' ? '*' : m[2];
  const fn = OP[op];
  if (!fn || (op === '/' && b === 0)) return null;
  const r = fn(a, b);
  return `${a} ${op} ${b} = ${Number.isInteger(r) ? r : r.toFixed(4)}`;
}

function num(s: string): number | null {
  const m = s.match(/(\d+\.?\d*)/);
  return m ? parseFloat(m[1]) : null;
}

function htmlSnippet(): string {
  return `Here's a clean starter HTML page:
\`\`\`html
<!doctype html>
<html>
<head><meta name="viewport" content="width=device-width, initial-scale=1"><title>My Page</title>
<style>body{font-family:system-ui;display:grid;place-items:center;min-height:100vh;background:#0a0d14;color:#e6edf3}</style>
</head>
<body><h1>Hello, VirgoYT! 🚀</h1></body>
</html>
\`\`\`
Tell me to **create this file** in your workspace (open File Manager → new file) and I'll add more.`;
}

function pythonServer(): string {
  return `Here's a minimal Python HTTP server:
\`\`\`python
from http.server import HTTPServer, BaseHTTPRequestHandler

class H(BaseHTTPRequestHandler):
    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()
        self.wfile.write(b"<h1>Hello from VirgoYT!</h1>")

HTTPServer(("0.0.0.0", 8080), H).serve_forever()
\`\`\`
Save as \`server.py\` and run with: \`python3 server.py\``;
}

function reactComponent(): string {
  return `Here's a reusable React component:
\`\`\`jsx
export default function Card({ title, body }) {
  return (
    <div style={{ border: '1px solid #3375ff', borderRadius: 12, padding: 16 }}>
      <h3>{title}</h3>
      <p>{body}</p>
    </div>
  );
}
\`\`\`
Use it: \`<Card title="Hello" body="from VirgoYT" />\``;
}
