import { Modal } from "ant-design-vue";
export default function myConfirm(){
    return new Promise((resolve,reject)=>{
        Modal.confirm({
            title:'提示',
            okText:'确定',
            okType:'danger',
            content:'确定删除吗？',
            cancelText:'取消',
            onOk:()=>{
                resolve(true)
            },
            onCancel(){
                reject(false)
            }
        })
    }).catch(()=>{
        return false
    })
}