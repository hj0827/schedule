<template>
  <div class="login-container">
    <a-form class="loginForm">
      <a-form-item>
        <span class="loginTitle">
          安桥排课系统
        </span>
      </a-form-item>
      <a-form-item v-bind="validateInfos.username">
        <a-input v-model:value="loginModel.username" placeholder="请输入用户名">
          <template #prefix>
            <a-icon type="user" />
          </template>
        </a-input>
      </a-form-item>
      <a-form-item v-bind="validateInfos.password">
        <a-input-password v-model:value="loginModel.password" placeholder="请输入密码" :visibility-toggle="true">
          <template #prefix>
            <a-icon type="lock" />
          </template>
        </a-input-password>
      </a-form-item>
      <a-form-item>
        <a-row :gutter="[16,16]">
          <a-col :span="12">
            <a-button type="primary" class="Btn loginBtn" @click="loginBtn">
              登录
            </a-button>
          </a-col>
          <a-col :span="12">
            <a-button @click="resetBtn" class="Btn resetBtn">
              重置
            </a-button>
          </a-col>
        </a-row>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { loginApi } from '@/api/login/login';
import { setToken, setUserId, setUserName, setName } from '@/utils/auth';
import { useForm } from 'ant-design-vue/lib/form';
import { useRouter } from 'vue-router';
import { DocumentTitle } from '@/type/BaseEnum';

const router = useRouter()
const loginModel = reactive({
  username: '',
  password: ''
})

// 表单验证
const rules = reactive({
  username: [
    { required: true, message: '请输入用户名', trigger: 'change' },
    // { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'change' },
    // { min: 6, max: 10, message: '长度在 6 到 10 个字符', trigger: 'blur'}
  ]
})

// 获取表单验证属性
const { validateInfos, validate, clearValidate } = useForm(loginModel, rules)

// 登录按钮
const loginBtn = () => {
  validate().then(async () => {
    let res = await loginApi(loginModel) as any
    console.log("登录返回的数据" + JSON.stringify(res))
    if (res && res.code == 200) {
      // 存储token
      setToken(res.data.token)
      // 存储用户id
      setUserId(res.data.userId)
      // 存储用户名
      setUserName(res.data.username)
      setName(res.data.name)
      // 跳转到首页
      router.push({ path: '/' })
    }
  }).catch(() => {
    console.log('登录失败')
  })
}

// 重置
const resetBtn = () => {
  loginModel.username = ''
  loginModel.password = ''
  clearValidate()
}

onMounted(() => {
  document.title = DocumentTitle.login; // 设置页面标题
})
</script>

<style scoped lang="scss">
.login-container {
  height: 100%;
  background-color: #fff;
  /* background-image: url(@/assets/images/login-bg.jpg); */
  display: flex;
  align-items: center;
  justify-content: center;
  background-size: 100% 100%;
}

.loginForm {
  height: 310px;
  width: 450px;
  background-color: #fff;
  padding: 35px 20px;
  border-radius: 10px;
  box-shadow: 0 0 10px #ccc;
}

.loginTitle {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #1890FF;
}

.Btn {
  width: 100%;
}
</style>