import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig } from 'axios'

const axiosInstance: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
axiosInstance.interceptors.response.use(
  response => response.data,
  error => Promise.reject(error)
)

function request<T = any>(config: AxiosRequestConfig) {
  return axiosInstance(config) as Promise<T>
}

export default request 