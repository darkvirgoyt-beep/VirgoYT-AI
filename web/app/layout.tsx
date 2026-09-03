import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';

const inter = Inter({ subsets: ['latin'], variable: '--font-inter' });

export const metadata: Metadata = {
  title: 'VirgoYT Cloud AI — Autonomous Cloud Computer',
  description:
    'A cloud AI computer. Write code, run a real Linux terminal, browse the web, and build full apps from a holographic 3D workspace.',
  keywords: ['AI', 'cloud computer', 'IDE', 'sandbox', 'development', 'VirgoYT'],
  authors: [{ name: 'darkvirgoyt-beep', url: 'https://github.com/darkvirgoyt-beep' }],
  openGraph: {
    title: 'VirgoYT Cloud AI',
    description: 'Your autonomous cloud AI computer',
    type: 'website',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`${inter.variable} font-sans`}>
        <div
          className="fixed inset-0 -z-10"
          style={{
            background:
              'radial-gradient(ellipse 80% 50% at 50% -20%, rgba(51,117,255,0.15), transparent), radial-gradient(ellipse 60% 40% at 80% 100%, rgba(185,103,255,0.1), transparent), #05060f',
          }}
        />
        {children}
      </body>
    </html>
  );
}
