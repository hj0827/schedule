import { ref } from "vue"
import { EditType } from "../../type/BaseEnum"
import { ListParm, ScheduleModel } from "@/api/schedule/ScheduleType"
import { message } from 'ant-design-vue'
import { FuncList } from "../../type/BaseType"
import useInstance from "../../hooks/useInstance"
import { deleteCalendarApi } from "@/api/schedule/schedule"

interface ApiResponse {
    code: number;
    data?: any;
    msg?: string;
}

interface MergeForm {
    courseId: string;
    courseName: string;
    majorName: string;
    courseType: string;
    courseYear: string;
    courseColor: string;
    type: string;
    isMergeClasses: string;
}

export default function useSchedule(getList: FuncList, listParm: ListParm, selectedRowKeys: any) {
    const { global } = useInstance()
    // 新增弹窗的ref属性
    const addRef = ref<{ show: (type: string, row?: ScheduleModel) => void }>()

    // 编辑
    const editBtn = (row: ScheduleModel) => {
        console.log("编辑", JSON.stringify(row, null, 2));
        addRef.value?.show(EditType.EDIT, row)
    }

    // 删除
    const deleteBtn = async (row: ScheduleModel) => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            let res = await deleteCalendarApi(String(row.id)) as ApiResponse
            if (res && res.code === 200) {
                // 提示
                message.success(res.msg)
                // 重新加载列表
                getList()
            } else {
                message.error(res.msg)
            }
        }
    }

    // 搜索
    const searchBtn = () => {
        getList()
    }

    // 重置按钮
    const resetBtn = () => {
        // 重置查询参数
        Object.assign(listParm, {
            courseName: '',
            courseType: '',
            teacherIdList: [],
            startDate: '',
            endDate: '',
            beginTime: '',
            endTime: '',
            teacherName: '',
        });
        // 重新加载数据
        getList();
    }

    // 删除选中课程
    const deleteSelectedUsers = async () => {
        const confirm = await global.$myconfirm();

        if (selectedRowKeys.value.length === 0) {
            message.warning("请选中要删除的数据");
            return;
        }
        if (confirm) {
            let allDeletedSuccessfully = true;
            for (const key of selectedRowKeys.value) {
                try {
                    const res = await deleteCalendarApi(String(key)) as ApiResponse;
                    if (res && res.code === 200) {
                        console.log(`Course with key ${key} deleted successfully.`);
                        message.success(`课程 ${key} 删除成功`);
                    } else {
                        console.error(`Failed to delete course with key ${key}:`, res.msg);
                        message.error(`课程 ${key} 删除失败: ${res.msg}`);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete course with key ${key}:`, error);
                    message.error(`课程 ${key} 删除失败: ${(error as Error).message || '未知错误'}`);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的课程都已成功删除");
                // 刷新列表
                getList();
                // 清除选中的内容
                selectedRowKeys.value = [];
            } else {
                message.error("部分课程删除失败，请检查日志");
            }
        }
    }

    return {
        editBtn,
        deleteBtn,
        addRef,
        searchBtn,
        resetBtn,
        deleteSelectedUsers
    }
}