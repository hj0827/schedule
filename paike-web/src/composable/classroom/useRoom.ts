import { ref } from "vue"
import { EditType } from "../../type/BaseEnum"
import { RoomModel } from "../../api/classroom/RoomType"
import { addApi, deleteApi, editApi } from "../../api/classroom/classroom"
import { message } from "ant-design-vue";
import useInstance from "../../hooks/useInstance";

interface ApiResponse {
    code: number;
    data?: any;
    msg?: string;
}

export default function useRoom(getList: Function) {
    // 获取全局属性
    const { global } = useInstance()
    const addRef = ref<{ show: (type: string, row?: RoomModel) => void }>()

    // 选中的教室ID列表
    const selectedRoomIds = ref<string[]>([])

    // 新增
    const addBtn = () => {
        addRef.value?.show(EditType.ADD)
    }

    // 编辑
    const editBtn = (row: RoomModel) => {
        addRef.value?.show(EditType.EDIT, row)
    }

    // 删除
    const deleteBtn = async (row: RoomModel) => {
        // 信息确认
        const confirm = await global.$myconfirm()
        if (confirm) {
            let res = await deleteApi(row.roomId) as ApiResponse
            if (res && res.code === 200) {
                message.success(res.msg)
                // 刷新列表
                getList()
            }
        }
    }

    // 保存
    const save = async (parm: RoomModel) => {
        let res: ApiResponse | null = null

        if (parm.type === EditType.ADD) {
            res = await addApi(parm) as ApiResponse
        } else {
            res = await editApi(parm) as ApiResponse
        }

        // 刷新列表
        if (res && res.code === 200) {
            message.success(res.msg)
            // 刷新列表
            getList()
        }
    }

    // 删除选中的教室
    const deleteSelectedRooms = async () => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            if (selectedRoomIds.value.length === 0) {
                message.warning("请选中要删除的数据");
                return;
            }
            let allDeletedSuccessfully = true;
            for (const roomId of selectedRoomIds.value) {
                try {
                    const res = await deleteApi(roomId) as ApiResponse;
                    if (res && res.code === 200) {
                        console.log(`Room with ID ${roomId} deleted successfully.`);
                    } else {
                        console.error(`Failed to delete room with ID ${roomId}:`, res.msg);
                        message.error(`教室 ${roomId} 删除失败: ${res.msg}`);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete room with ID ${roomId}:`, error);
                    message.error(`教室 ${roomId} 删除失败: ${(error as Error).message || '未知错误'}`);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的教室都已成功删除");
                // 刷新列表
                getList();
                // 清除选中的内容
                selectedRoomIds.value = [];
            } else {
                message.error("部分教室删除失败，请检查日志");
            }
        }
    };

    // 处理行选择变化
    const onSelectChange = (selectedRowKeys: string[]) => {
        selectedRoomIds.value = selectedRowKeys;
    };

    return {
        addBtn,
        editBtn,
        deleteBtn,
        save,
        addRef,
        deleteSelectedRooms,
        onSelectChange,
        selectedRoomIds
    }
}