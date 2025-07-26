import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import Layout from '../layout/index.vue';
import AnXiaoQiao from '@/views/anxiaoqiao/AnXiaoQiao.vue';
export const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        component: () => import('../layout/dashboard/Index.vue'),
        name: 'dashboard',
        meta: {
          title: '首页',
          icon: '#icondashboard',
        },
      },
      {
        path: '/anxiaoqiao',
        component: AnXiaoQiao,
        name: 'AnXiaoQiao',
        meta: {
          title: '安小桥°', 
          icon: '#iconanxiaoqiao',
          badge: 'AI',
        },
      },
    ],
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/login/Login.vue'),
  },
  // {
  //   path: '/system',
  //   component: Layout,
  //   name: 'system',
  //   meta: {
  //     title: '系统管理',
  //     icon: 'el-icon-menu',
  //     roles: ['sys:manage'],
  //     parentId: 0,
  //   },
  //   children: [
  //     {
  //       path: '/user',
  //       component: () => import('../views/system/user/User.vue'),
  //       name: 'user',
  //       meta: {
  //         title: '用户管理',
  //         icon: 'el-icon-s-custom',
  //         roles: ['sys:user'],
  //       },
  //     },
  //     {
  //       path: '/role',
  //       component: () => import('../views/system/role/Role.vue'),
  //       name: 'role',
  //       meta: {
  //         title: '角色管理',
  //         icon: 'el-icon-s-tools',
  //         roles: ['sys:role'],
  //       },
  //     },
  //     {
  //       path: '/menu',
  //       component: () => import('../views/system/menu/Menu.vue'),
  //       name: 'menu',
  //       meta: {
  //         title: '菜单管理',
  //         icon: 'el-icon-document',
  //         roles: ['sys:menu'],
  //       },
  //     },
  //   ],
  // },
  // {
  //   path: '/classroomRoot',
  //   component: Layout,
  //   name: 'classroomRoot',
  //   meta: {
  //     title: '教室管理',
  //     icon: 'el-icon-menu',
  //     roles: ['sys:manage'],
  //     parentId: 0,
  //   },
  //   children: [
  //     {
  //       path: '/classroom',
  //       component: () => import('../views/classroom/Classroom.vue'),
  //       name: 'classroom',
  //       meta: {
  //         title: '教室列表',
  //         icon: 'el-icon-s-custom',
  //         roles: ['sys:user'],
  //       },
  //     },
  //   ],
  // },
  // {
  //   path: '/courseRoot',
  //   component: Layout,
  //   name: 'courseRoot',
  //   meta: {
  //     title: '课程管理',
  //     icon: 'el-icon-menu',
  //     roles: ['sys:manage'],
  //     parentId: 0,
  //   },
  //   children: [
  //     {
  //       path: '/course',
  //       component: () => import('../views/course/Course.vue'),
  //       name: 'course',
  //       meta: {
  //         title: '课程列表',
  //         icon: 'el-icon-s-custom',
  //         roles: ['sys:user'],
  //       },
  //     },
  //   ],
  // },
  // {
  //   path: '/teacherRoot',
  //   component: Layout,
  //   name: 'teacherRoot',
  //   meta: {
  //     title: '教师管理',
  //     icon: 'el-icon-menu',
  //     roles: ['sys:manage'],
  //     parentId: 0,
  //   },
  //   children: [
  //     {
  //       path: '/teacher',
  //       component: () => import('../views/teacher/Teacher.vue'),
  //       name: 'teacher',
  //       meta: {
  //         title: '教师列表',
  //         icon: 'el-icon-s-custom',
  //         roles: ['sys:user'],
  //       },
  //     },
  //   ],
  // },
  {
    path: '/scheduleRoot',
    component: Layout,
    name: 'scheduleRoot',
    meta: {
      title: '排课管理',
      icon: 'AppstoreOutlined',
      roles: ['sys:manage'],
      parentId: 0,
    },
    children: [
      {
        path: '/schedule',
        component: () => import('../views/schedule/Schedule.vue'),
        name: 'schedule',
        meta: {
          title: '排课日历',
          icon: 'CalendarOutlined',
          roles: ['sys:user'],
        },
      },
      {
        path: '/scheduleList',
        component: () => import('../views/schedule/ScheduleList.vue'),
        name: 'scheduleList',
        meta: {
          title: '排课列表',
          icon: 'UnorderedListOutlined',
          roles: ['sys:user'],
        },
      },
      {
        path: '/statistics',
        component: () => import('../views/schedule/Statistics.vue'),
        name: 'statistics',
        meta: {
          title: '课时统计',
          icon: 'BarChartOutlined',
          roles: ['sys:user'],
        },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;