import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';


// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 8080,
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8089', // 目标后端 API 地址
        changeOrigin: true, // 支持虚拟托管
        rewrite: (path) => path.replace(/^\/api/, ''), // 重写路径
      },
    },
  },
  resolve: {
    alias: [
      {
        find: '@',
        replacement: resolve(__dirname, 'src'),
      },
    ],
  },
  css: {
    preprocessorOptions: {
      scss: {
        api: 'modern-compiler',
      },
    },
  },
  build: {
    rollupOptions: {
      onwarn(warning, warn) {
        // 忽略所有警告
        if (warning.code !== 'SOURCEMAP_ERROR') {
          return;
        }
        warn(warning);
      },
    },
  },
});