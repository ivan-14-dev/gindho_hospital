import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  publicDir: 'public',
  build: {
    target: 'es2020',
    minify: 'esbuild',
    cssCodeSplit: true,
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (id.includes('node_modules')) {
            if (id.includes('react') || id.includes('react-dom')) {
              return 'vendor';
            }
            if (id.includes('react-router')) {
              return 'router';
            }
            if (id.includes('@tanstack/react-query')) {
              return 'query';
            }
            if (id.includes('lucide-react') || id.includes('@radix-ui')) {
              return 'ui';
            }
            if (id.includes('recharts')) {
              return 'charts';
            }
          }
        },
      },
    },
    chunkSizeWarningLimit: 300,
  },
  define: {
    __BUNDLE_BUDGET__: JSON.stringify({
      js: 300 * 1024,
      css: 50 * 1024,
      total: 400 * 1024,
    }),
  },
})