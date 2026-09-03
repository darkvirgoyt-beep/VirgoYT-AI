import type { Config } from 'tailwindcss';

const config: Config = {
  content: [
    './app/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
    './hooks/**/*.{js,ts,jsx,tsx,mdx}',
    './stores/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        virgo: {
          50: '#eef5ff',
          100: '#d9e8ff',
          200: '#bcd6ff',
          300: '#8ebdff',
          400: '#5999ff',
          500: '#3375ff',
          600: '#1d53f5',
          700: '#1640e1',
          800: '#1836b6',
          900: '#1a338f',
          950: '#142256',
        },
        void: {
          950: '#05060f',
          900: '#0a0c1a',
          800: '#111327',
          700: '#1a1d36',
          600: '#252849',
        },
        terminal: {
          green: '#00ff9c',
          amber: '#ffb547',
          red: '#ff5c7a',
          cyan: '#00d4ff',
          purple: '#b967ff',
          white: '#e6edf3',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'monospace'],
      },
      animation: {
        'pulse-slow': 'pulse 4s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'float': 'float 6s ease-in-out infinite',
        'spin-slow': 'spin 12s linear infinite',
        'glow': 'glow 2s ease-in-out infinite',
      },
      keyframes: {
        float: {
          '0%, 100%': { transform: 'translateY(0px)' },
          '50%': { transform: 'translateY(-12px)' },
        },
        glow: {
          '0%, 100%': { boxShadow: '0 0 5px rgba(51,117,255,0.5)' },
          '50%': { boxShadow: '0 0 20px rgba(51,117,255,0.9)' },
        },
      },
      backdropBlur: {
        xs: '2px',
      },
    },
  },
  plugins: [],
};

export default config;
