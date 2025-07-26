import { ref } from "vue"
import { EditType } from "../../type/BaseEnum"
import { ListParm, ScheduleModel } from "@/api/schedule/ScheduleType"
import { message } from 'ant-design-vue'
import { FuncList } from "../../type/BaseType"
import useInstance from "../../hooks/useInstance"
import { deleteCalendarApi, deleteCalendarListApi } from "@/api/schedule/schedule"

export interface ApiResponse {
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
    const loading1 = ref(false) // 添加 loading 状态
    // 新增弹窗的ref属性
    const addRef = ref<{ show: (type: string, row?: ScheduleModel) => void }>()

    // 编辑
    const editBtn = (row: ScheduleModel) => {
        // 传递完整的数据给编辑组件, including isParent flag and children if available
        addRef.value?.show(EditType.EDIT, row);
    }

    // 删除
    const deleteBtn = async (row: ScheduleModel) => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            try {
                loading1.value = true;
                // 如果是父节点，使用批量删除接口
                const res = await deleteCalendarApi(String(row.id)) as ApiResponse;
                if (res && res.code === 200) {
                    message.success(res.msg || '删除成功');
                    getList();
                } else {
                    message.error(res.msg || '删除失败');
                }
            } catch (error) {
                console.error('删除失败:', error);
                message.error('删除失败，请稍后重试');
            } finally {
                loading1.value = false;
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
        if (selectedRowKeys.value.length === 0) {
            message.warning("请选中要删除的数据");
            return;
        }

        const confirm = await global.$myconfirm();
        if (confirm) {
            loading1.value = true;
            try {
                let allDeletedSuccessfully = true;
                for (const id of selectedRowKeys.value) {
                    try {
                        // 判断是否是父节点ID（包含分隔符）
                        const isParent = typeof id === 'string' && id.includes('-');
                        const res = isParent ? 
                            await deleteCalendarListApi(String(id)) as ApiResponse :
                            await deleteCalendarApi(String(id)) as ApiResponse;

                        if (res && res.code === 200) {
                            console.log(`课程 ${id} 删除成功`);
                        } else {
                            console.error(`删除课程 ${id} 失败:`, res.msg);
                            allDeletedSuccessfully = false;
                        }
                    } catch (error) {
                        console.error(`删除课程 ${id} 失败:`, error);
                        allDeletedSuccessfully = false;
                    }
                }

                if (allDeletedSuccessfully) {
                    message.success("所有选中的课程都已成功删除");
                    getList();
                    selectedRowKeys.value = [];
                } else {
                    message.error("部分课程删除失败，请检查日志");
                }
            } finally {
                loading1.value = false;
            }
        }
    }

    return {
        editBtn,
        deleteBtn,
        addRef,
        searchBtn,
        resetBtn,
        deleteSelectedUsers,
        loading1
    }
}