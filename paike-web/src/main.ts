import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
// 引入antd design vue
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

//引入路由
import router from './router'

// 对象复制
import objCoppy from './utils/objCoppy';

// 信息确认
import myconfirm from './utils/myconfirm';

// 引入图标,起别名为Icons
import * as Icons from '@ant-design/icons-vue'


// 引入pinia
import { createPinia } from 'pinia'
const pinia = createPinia()


const app = createApp(App)

// 引入按钮权限
import { permission } from '@/directives/permission'

// 全局挂载
app.config.globalProperties.$objCoppy = objCoppy
app.config.globalProperties.$myconfirm = myconfirm

// 注册图标
Object.keys(Icons).forEach((key) => {
    app.component(key, Icons[key as keyof typeof Icons])
})
app.use(pinia)
    .use(router)
    .use(Antd)
    .mount('#app')
// 注册按钮权限
app.directive('permission', permission)
// 权限验证
import './permission'