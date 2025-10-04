/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,jsx,ts,tsx}'
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          50: '#eef6ff',
          100: '#d9ecff',
          200: '#bce0ff',
          300: '#8ccaff',
          400: '#55adff',
          500: '#2a8bff',
          600: '#0065e6',
          700: '#004fc0',
          800: '#003f96',
          900: '#003579'
        }
      }
    }
  },
  plugins: [],
};
