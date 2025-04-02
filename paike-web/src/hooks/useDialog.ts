
import { reactive } from "vue"
// 定义 DialogModel 接口
interface DialogModel {
    title: string;
    visible: boolean;
    width: number;
    height: number;
}


export default function useDialog() {
    // 弹窗属性
    const dialog = reactive<DialogModel>({
        title: '',
        visible: false,
        width: 650,
        height: 250
    })
    // 确定事件
    const onConfirm = () => {
        dialog.visible = false
    }

    // 取消
    const onClose = () => {
        dialog.visible = false
    }
    // 显示弹窗
    const onShow = ()=>{
        dialog.visible = true
    }
    return {
        dialog,
        onConfirm,
        onClose,
        onShow
    }
}