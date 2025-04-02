import { MenuModel } from "@/api/menu/MenuType";
import { EditType } from "@/type/BaseEnum";
import { ref } from "vue";
import useInstance from "@/hooks/useInstance";
import { deleteApi } from "@/api/menu/menu";
import { FuncList } from "@/type/BaseType";
import { message } from "ant-design-vue";

export default function useMenu(refresh:FuncList) {
    const {global} = useInstance()
    // 弹窗ref属性
    const addRef= ref()
    //    新增
    const addBtn = () => {
        addRef.value.show(EditType.ADD)
    }
    // 编辑
    const editBtn = (row:MenuModel) => {
        addRef.value.show(EditType.EDIT,row)
    }

    // 删除
    const deleteBtn = async(row:MenuModel) => {
        // 信息确认
        const confirm = await global.$myconfirm()
        if(confirm){
            let res = await deleteApi(row.menuId) as any
            if(res.code === 200){
                message.success(res.msg)
                // 刷新列表
                refresh()
            }else{
                message.error(res.msg)
            }
        }
    }


    return {
        addBtn,
        editBtn,
        deleteBtn,
        addRef
    }
}