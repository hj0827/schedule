import { ref, reactive } from "vue"
import { CourseModel, ListParm } from "../../api/course/BaseCourse"
import { EditType } from "../../type/BaseEnum"
import { addCourseApi, editCourseApi, deleteCourseApi } from '../../api/course/course'
import { message } from 'ant-design-vue'
import { FuncList } from "../../type/BaseType"
import useInstance from "../../hooks/useInstance"

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

export default function useCourse(getList: FuncList, listParm: ListParm) {
    const { global } = useInstance()
    // 新增弹窗的ref属性
    const addRef = ref<{ show: (type: string, row?: CourseModel) => void }>()

    // 选中的课程ID列表
    const selectedCourseIds = ref<string[]>([])

    // 新增
    const addBtn = () => {
        addRef.value?.show(EditType.ADD)
    }

    // 编辑
    const editBtn = (row: CourseModel) => {
        addRef.value?.show(EditType.EDIT, row)
    }

    // 删除
    const deleteBtn = async (row: CourseModel) => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            let res = await deleteCourseApi(row.courseId) as ApiResponse
            if (res && res.code === 200) {
                // 提示
                message.success(res.msg)
                // 重新加载列表
                getList()
            }
        }
    }

    // 保存
    const save = async (items: CourseModel[]) => {
        console.log("保存的数据", items);
        let res: ApiResponse | null = null;
        try {
            if (items.length > 0) {
                // 将所有课程数据打包成一个数组
                if (items[0].type === EditType.ADD) {
                    res = await addCourseApi(items) as ApiResponse;
                } else if (items[0].type === EditType.EDIT) {
                    res = await editCourseApi(items) as ApiResponse;
                }
                
                // 刷新列表
                if (res && res.code === 200) {
                    message.success(res.msg);
                    getList();
                }
            } else {
                console.warn("没有数据需要保存");
            }
        } catch (error) {
            console.error('保存课程时发生错误:', error);
        }
    };

    // 删除选中的课程
    const deleteSelectedCourses = async () => {
        const confirm = await global.$myconfirm();
        if (confirm) {
            if (selectedCourseIds.value.length === 0) {
                message.warning("请选中要删除的数据");
                return;
            }
            let allDeletedSuccessfully = true;
            for (const courseId of selectedCourseIds.value) {
                try {
                    const res = await deleteCourseApi(courseId) as ApiResponse;
                    if (res && res.code === 200) {
                        console.log(`Course with ID ${courseId} deleted successfully.`);
                    } else {
                        console.error(`Failed to delete course with ID ${courseId}:`, res.msg);
                        message.error(`课程 ${courseId} 删除失败: ${res.msg}`);
                        allDeletedSuccessfully = false;
                    }
                } catch (error) {
                    console.error(`Failed to delete course with ID ${courseId}:`, error);
                    message.error(`课程 ${courseId} 删除失败: ${(error as Error).message || '未知错误'}`);
                    allDeletedSuccessfully = false;
                }
            }

            if (allDeletedSuccessfully) {
                message.success("所有选中的课程都已成功删除");
                // 重新加载列表
                getList();
                // 清除选中的内容
                selectedCourseIds.value = [];
            } else {
                message.error("部分课程删除失败，请检查日志");
            }
        }
    };

    const rolePage = reactive({
        current: 1,
        pageSize: 10,
        total: 0,
        showSizeChanger: true,
        pageSizeOptions: ['10', '20', '30', '40', '50'],
        showTotal: (total: number) => `共有${total}条数据`,
        onChange: (current: number, size: number) => {
            listParm.currentPage = current;
            listParm.pageSize = size;
            rolePage.current = current;
            rolePage.pageSize = size;
            getList();
        }
    });

    // 搜索
    const searchBtn = () => {
        rolePage.current = 1; 
        listParm.currentPage = 1; 
        getList(); 
    }

    // 重置按钮
    const resetBtn = () => {
        listParm.courseName = ''
        listParm.courseType = ''
        listParm.currentPage = 1
        getList()
    }

    // 处理行选择变化
    const onSelectChange = (selectedRowKeys: string[]) => {
        selectedCourseIds.value = selectedRowKeys;
    };

    return {
        addBtn,
        editBtn,
        deleteBtn,
        save,
        addRef,
        searchBtn,
        resetBtn,
        deleteSelectedCourses,
        onSelectChange,
        selectedCourseIds
    }
}