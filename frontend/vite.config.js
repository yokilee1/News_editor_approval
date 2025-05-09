// vite.config.js
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    server: {
        port: 3000, // 前端开发服务器运行在 3000 端口
        proxy: {
            '/api': {
                target: 'http://localhost:8081', // 后端 API 地址
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '/api'), // 保持 API 路径一致
            },
        },
    },
    resolve: {
        alias: {
            '@': '/src', // 配置 @ 指向 src 目录，简化导入路径
        },
    },
});