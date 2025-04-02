import { RoleType } from "@/api/role/RoleType"
import { EditType } from "@/type/BaseEnum"
import { ref } from "vue"
import { addApi, editApi, deleteApi } from "@/api/role/role"
import { message } from "ant-design-vue"
import { FuncList } from "@/type/BaseType"
import useInstance from "@/hooks/useInstance"

interface ApiResponse {
    code: number;
    msg?: string;
}

export default function useRole(getList: FuncList, selectedRowKeys: any) {
    // 分配权限的ref属性
    const assignRef = ref()
    const { global } = useInstance()
    // 弹窗的ref属性
    const addRef = ref()
    // 新增
    const addBtn = () => {
        addRef.value.show(EditType.ADD)
    }
    // 编辑
    const editBtn = (row: RoleType) => {
        addRef.value.show(EditType.EDIT, row)
    }

    // 删除
    const deleteBtn = async (row: RoleType) => {
        // 信息确定
        const confirm = await global.$myconfirm()
        if (confirm) {
            const res = await deleteApi(row.roleId) as ApiResponse
            if (res.code === 200) {
                message.success(res.msg)
                // 刷新表格
                getList()
            } else {
                message.error(res.msg)
            }
        }
    }

    // 删除选中角色
    const deleteSelectedRoles = async () => {
        const confirm = await global.$myconfirm();

        if (selectedRowKeys.value.length === 0) {
            message.warning("请选中要删除的角色");
            return;
        }
        if (confirm) {
            let allDeletedSuccessfully = true;
            for (const key of selectedRowKeys.value) {
                try {
                    const res = await deleteApi(key) as ApiResponse;
                    if (res.code === 200) {
                        console.log(`Role with key ${key} deleted successfully.`);
                    } else {
                        console.error(`Failed to delete role with key ${key}:`, res.msg);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete role with key ${key}:`, error);
                    message.error(`角色 ${key} 删除失败: ${(error as Error).message || '未知错误'}`);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的角色都已成功删除");
                // 刷新表格
                getList();
                // 清除选中的内容
                selectedRowKeys.value = [];
            } else {
                message.error("部分角色删除失败，请检查日志");
            }
        }
    }

    // 保存
    const handleApiResponse = async <T>(promise: Promise<T>): Promise<ApiResponse> => {
        try {
            const response = await promise;
            return { code: 200, msg: JSON.stringify(response) };
        } catch (error) {
            return { code: 500, msg: (error as Error).message || '未知错误' };
        }
    };

    // 修改 useRole.ts 中的 save 函数
    const save = async (data: RoleType) => {
        console.log(data);
        let res: ApiResponse | undefined;

        if (data.type === EditType.ADD) {
            res = await addApi(data) as any;
        } else if (data.type === EditType.EDIT) {
            res = await editApi(data) as any;
        }

        if (res && res.code === 200) {
            message.success(res.msg);
            // 刷新表格
            getList();
        } else if (res) {
            message.error(res.msg);
        } else {
            message.error("操作失败");
        }
    };

    // 分配权限按钮
    const assignBtn = (row: RoleType) => {
        assignRef.value.show(row)
    }

    return {
        addBtn,
        editBtn,
        deleteBtn,
        save,
        addRef,
        assignRef,
        assignBtn,
        deleteSelectedRoles
    }
}