/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        primary: '#ff335f',
        primaryLight: '#fa6988',
        success: '#00b365',
        warning: '#ff8800',
        error: '#f5483b',
        'base-color': '#121316',
        color: 'rgba(18, 19, 22, 0.8)',
        secondary: 'rgba(18, 19, 22, 0.5)',
        tertiary: 'rgba(18, 19, 22, 0.25)',
        quaternary: 'rgba(18, 19, 22, 0.20)',
        background: '#141414',
        backgroundSecondary: '#e0e0e0',
        // ICON colors
        icon: '#4A4653',
        fill: '#B4B6BC', // primary fill color
        'fill-secondary': '#FAF3C0', // secondary fill color
        'fill-tertiary': '#F4F5F9', // tertiary fill color
        'fill-quaternary': '#f9f9f9', // quaternary fill color
        colorBorder: '#D7D8DD',
        borderSecondary: '#EBECF0',
        borderRadius: 2,
      },
      transitionTimingFunction: {
        'in-expo': 'cubic-bezier(0.95, 0.05, 0.795, 0.035)',
        'out-expo': 'cubic-bezier(0.19, 1, 0.22, 1)',
      },
    },
  },
  plugins: [],
  corePlugins: {
    preflight: false,
  },
};
