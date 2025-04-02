import { UserType } from "@/api/user/UserType"
import { EditType } from "@/type/BaseEnum"
import { ref } from 'vue'
import { addUserApi } from "@/api/user/user"
import { message } from "ant-design-vue"
import { FuncList } from "@/type/BaseType"
import { deleteUserApi } from "@/api/user/user"
import useInstance from "@/hooks/useInstance"

export default function useUser(getList: FuncList, selectedRowKeys: any) {
    const { global } = useInstance()
    // 弹窗属性
    const addRef = ref()
    const importRef = ref()
    // 新增
    const addBtn = () => {
        addRef.value.show(EditType.ADD)
    }
    // 编辑
    const editBtn = (row: any) => {
        addRef.value.show(EditType.EDIT, row)
    }
    // 删除
    const deleteBtn = async (row: any) => {
        const confirm = await global.$myconfirm()
        if (confirm) {
            let res = await deleteUserApi(row.userId) as any
            if (res && res.code == 200) {
                message.success(res.msg)
                // 刷新列表
                getList()
            } else {
                message.error(res.msg)
            }
        }
    }
    // 保存
    const save = async (parm: UserType) => {
        console.log(parm)
    }
    // 导入
    const importBtn = async () => {
        importRef.value.show(EditType.ImportUser)
    }

    const deleteSelectedUsers = async () => {
        const confirm = await global.$myconfirm()

        if (selectedRowKeys.value.length === 0) {
            message.warning("请选中要删除的数据");
            return;
        }
        if (confirm) {
            let allDeletedSuccessfully = true;
            for (const key of selectedRowKeys.value) {
                try {
                    const res = await deleteUserApi(key) as any;
                    if (res && res.code === 200) {
                        console.log(`User with key ${key} deleted successfully.`);
                    } else {
                        console.error(`Failed to delete user with key ${key}:`, res.msg);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete user with key ${key}:`, error);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的用户都已成功删除");
                // 刷新列表
                getList();
                // 清除选中的内容
                selectedRowKeys.value = [];
            } else {
                message.error("部分用户删除失败，请检查日志");
            }
        }
    }

    return {
        addRef,
        addBtn,
        editBtn,
        deleteBtn,
        save,
        importBtn,
        importRef,
        deleteSelectedUsers
    }
}