import { AxiosInstance, AxiosRequestConfig, AxiosResponse } from "axios";
import axios from 'axios';
import { message, Modal } from 'ant-design-vue';
import { h } from 'vue'

export interface Result<T = any> {
    code: number;
    msg: string;
    data: T;
}

class request {
    // axios实例
    private instance: AxiosInstance;
    // 构造函数里面初始化
    constructor(config: AxiosRequestConfig) {
        this.instance = axios.create(config)
        // 定义拦截器
        this.interceptors()
    }
    // 拦截器
    private interceptors() {
        // axios发送请求之前的处理
        this.instance.interceptors.request.use((config: AxiosRequestConfig) => {
            // 请求头部携带token
            let token = "";
            if (token) {
                // 把token放置在headers里面
                config.headers = {
                    ...config.headers,
                    token: token
                }
            }
            return config
        }, (error: any) => {
            error.data = {}
            error.data.msg = '服务器异常，请联系管理员'
            return error
        })
        // axios请求返回之后的处理
        this.instance.interceptors.response.use((res: AxiosResponse) => {
            console.log(res.data)
            if (res.data.code != 200) {
                if (res.data.code == 1001) {
                    const dataArray = Array.isArray(res.data.data) ? res.data.data : res.data.data.split(',');
                    Modal.warning({
                        title: '系统提示，排课时间冲突',
                        content: h('div', { class: 'scheduleClass' }, [
                            ...dataArray.map((item: string) => h('p', {}, item))
                        ]),
                        okText: '确定',
                        onOk: () => {
                            console.log('ok');
                        },
                    });
                } else {
                    message.error(res.data.msg || '服务器出错')
                    // 检查 res.data.data 是否存在且为数组
                    if (res.data.data && Array.isArray(res.data.data)) {
                        // 遍历出来
                        let datas: string[] = []
                        res.data.data.forEach((item: any) => {
                            console.log(item.name); // 控制台输出时换行
                            datas.push(item.name + "\n");
                        });
                        console.log(datas)
                        if (datas.length > 0) {
                            // message.error(datas)
                            Modal.warning({
                                title: '系统提示，用户名已存在',
                                content: h('div', { class: 'scheduleClass' }, [
                                    datas.map(username => h('p', username))
                                ]),
                                okText: '确定',
                                onOk: () => {
                                    console.log('ok');
                                },
                            });
                        }
                    }
            
                    return Promise.reject(res.data.msg || '服务器出错')
                }
            } else {
                return res.data
            }
        }, (error) => {
            console.log('进入错误')
            error.data = {};
            if (error && error.response) {
                switch (error.response.status) {
                    case 400:
                        error.data.msg = '错误请求'
                        message.error(error.data.msg)
                        break;
                    case 401:
                        error.data.msg = '未授权，请重新登录'
                        message.error(error.data.msg)
                        break;
                    case 403:
                        error.data.msg = '拒绝访问'
                        message.error(error.data.msg)
                        break;
                    case 404:
                        error.data.msg = '请求错误,未找到该资源'
                        message.error(error.data.msg)
                        break;
                    case 405:
                        error.data.msg = '请求方法未允许'
                        message.error(error.data.msg)
                        break;
                    case 408:
                        error.data.msg = '请求超时'
                        message.error(error.data.msg)
                        break;
                    case 500:
                        error.data.msg = '服务器端出错'
                        message.error(error.data.msg)
                        break;
                    case 501:
                        error.data.msg = '网络未实现'
                        message.error(error.data.msg)
                        break;
                    case 502:
                        error.data.msg = '网络错误'
                        message.error(error.data.msg)
                        break;
                    case 503:
                        error.data.msg = '服务不可用'
                        message.error(error.data.msg)
                        break;
                    case 504:
                        error.data.msg = '网络超时'
                        message.error(error.data.msg)
                        break;
                    case 505:
                        error.data.msg = 'http版本不支持该请求'
                        message.error(error.data.msg)
                        break;
                    default:
                        error.data.msg = `连接错误${error.response.status}`
                        message.error(error.data.msg)
                }
            } else {
                error.data.msg = "连接到服务器失败"
                message.error(error.data.msg)
            }
            return Promise.reject(error)
        })
    }
    service<T>(config: AxiosRequestConfig): Promise<T> {
        return new Promise((resolve, reject) => {
            this.instance.request<any, T>(config).then(res => {
                resolve(res)
            }).catch(err => {
                reject(err)
                return err
            })
        })
    }
    // get请求
    get<T>(config: AxiosRequestConfig): Promise<T> {
        return this.service({ ...config, method: 'GET' })
    }
    // post请求
    post<T>(config: AxiosRequestConfig): Promise<T> {
        return this.service({ ...config, method: 'POST' })
    }
    // delete请求
    delete<T>(config: AxiosRequestConfig): Promise<T> {
        return this.service({ ...config, method: 'DELETE' })
    }
    // put请求
    put<T>(config: AxiosRequestConfig): Promise<T> {
        return this.service({ ...config, method: 'PUT' })
    }
}

export default request