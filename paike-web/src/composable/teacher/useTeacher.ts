import { ref } from 'vue'
import { EditType } from '../../type/BaseEnum'
import { TeacherModel } from '../../api/teacher/TeacherType'
import { addTeacherApi, editApi, deleteApi } from '../../api/teacher/teacher'
import { message } from 'ant-design-vue'
import { FuncList } from '../../type/BaseType'
import useInstance from '../../hooks/useInstance'

export default function useTeacher(getList: FuncList) {
    interface ApiResponse {
        code: number;
        msg: string;
    }

    // 获取全局变量
    const { global } = useInstance()

    // 新增弹窗ref属性
    const addRef = ref<{ show: (type: string, row?: TeacherModel) => void }>()

    // 选中的教师ID列表
    const selectedTeacherIds = ref<string[]>([])

    // 新增
    const addBtn = () => {
        addRef.value?.show(EditType.ADD)
    }

    // 编辑
    const editBtn = (row: TeacherModel) => {
        addRef.value?.show(EditType.EDIT, row)
    }

    // 删除
    const deleteBtn = async (row: TeacherModel) => {
        // 信息确认
        const confirm = await global.$myconfirm()
        if (confirm) {
            let res = await deleteApi(row.teacherId) as ApiResponse
            if (res && (res as ApiResponse).code === 200) {
                message.success((res as ApiResponse).msg);
                // 刷新表格
                getList();
            }
        }
    }

    // 保存
    const save = async (data: TeacherModel) => {
        console.log('父组件接收数据')
        console.log(data)
        // 判断是新增还是编辑
        let res = null;
        if (data.type === EditType.ADD) {
            res = await addTeacherApi(data)
        } else {
            res = await editApi(data)
        }

        if (res && (res as ApiResponse).code === 200) {
            message.success((res as ApiResponse).msg);
            // 刷新表格
            getList();
        }
    }

    // 删除选中的教师
    const deleteSelectedTeachers = async () => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            if (selectedTeacherIds.value.length === 0) {
                message.warning("请选中要删除的数据");
                return;
            }
            let allDeletedSuccessfully = true;
            for (const teacherId of selectedTeacherIds.value) {
                try {
                    const res = await deleteApi(teacherId) as ApiResponse;
                    if (res && res.code === 200) {
                        console.log(`Teacher with ID ${teacherId} deleted successfully.`);
                    } else {
                        console.error(`Failed to delete teacher with ID ${teacherId}:`, res.msg);
                        message.error(`教师 ${teacherId} 删除失败: ${res.msg}`);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete teacher with ID ${teacherId}:`, error);
                    message.error(`教师 ${teacherId} 删除失败: ${(error as Error).message || '未知错误'}`);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的教师都已成功删除");
                // 刷新表格
                getList();
                // 清除选中的内容
                selectedTeacherIds.value = [];
            } else {
                message.error("部分教师删除失败，请检查日志");
            }
        }
    };

    // 处理行选择变化
    const onSelectChange = (selectedRowKeys: string[]) => {
        selectedTeacherIds.value = selectedRowKeys;
    };

    return {
        addBtn,
        editBtn,
        deleteBtn,
        save,
        addRef,
        deleteSelectedTeachers,
        onSelectChange,
        selectedTeacherIds
    }
}