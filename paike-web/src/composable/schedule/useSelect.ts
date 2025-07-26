import type { SelectProps } from 'ant-design-vue'
import { onMounted, ref } from 'vue';
import { getTeacherApi, getRoomListApi, getCourseListApi, getScheduleInfoApi } from '../../api/schedule/schedule'
import { FuncList } from '@/type/BaseType';
import { ListParm } from '@/api/schedule/ScheduleType';
export default function useselect(getList: FuncList, listParm: ListParm) {
    //教室列表数据
    const roomOptions = ref<SelectProps['options']>([

    ]);
    //教师列表数据
    const teacherOptions = ref<SelectProps['options']>([

    ]);
    // 课程列表数据
    const courseOptions = ref<SelectProps['options']>([

    ]);

    // 排课课程列表数据
    const courseOptionsPaike = ref<SelectProps['options']>([

    ]);

    //获取教师列表
    const teacher = async () => {
        let res = await getTeacherApi() as any
        if (res && res.code == 200) {
            teacherOptions.value = res.data
        }
    }
    //获取教室列表
    const room = async () => {
        let res = await getRoomListApi() as any
        if (res && res.code == 200) {
            roomOptions.value = res.data
        }
    }
    // 获取课程列表
    const course = async () => {
        let res = await getCourseListApi() as any;
        if (res && res.code == 200) {
            // console.log("课程列表结果111111111：" + JSON.stringify(res.data, null, 2));
            // 过滤出 parentCourseId 为空的数据，并且根据 isMergeClasses 判断是否合并
            courseOptions.value = res.data
                // .filter((item: any) => item.parentCourseId === null || item.parentCourseId === undefined || item.parentCourseId === '')
                .map((item: any) => ({
                    value: item.value,
                    label: `${item.label} - ${item.courseType}`, // 显示 course_id 和 course_type
                    isMergeClasses: item.isMergeClasses
                }));
        }
    }

    const coursePaike = async () => {
        let res = await getCourseListApi() as any;
        // console.log("课程列表结果：" + res.data[0].parentCourseId);
        if (res && res.code == 200) {
            // 过滤出 parentCourseId 为空的数据，并且根据 isMergeClasses 判断是否合并
            console.log("课程列表：" + JSON.stringify(res.data, null, 2));
            courseOptionsPaike.value = res.data
                .filter((item: any) => item.parentCourseId === null || item.parentCourseId === undefined || item.parentCourseId === '')
                .map((item: any) => ({
                    value: item.value,
                    label: `${item.label} - ${item.courseType}`,
                    courseType: res.data,  // 传递全部课程数据用于获取合并课程的考期
                    isMergeClasses: item.isMergeClasses
                }));
        }
    }

    // 获取已排课程的列表
    const getPaikeCourseList = async () => {
        let res = await getScheduleInfoApi(listParm) as any;
        if(res && res.code == 200){
            console.log("已排课程的列表：" + JSON.stringify(res.data, null, 2));
        }
    }

    const filterTeacheroption = (input: string, option: any) => {
        return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    }
    const filterRoomOption = (input: string, option: any) => {
        return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };
    const filterCourseOption = (input: string, option: any) => {
        return option.label.toLowerCase().indexOf(input.toLowerCase()) >= 0;
    };

    onMounted(() => {
        teacher()
        room()
        course()
        coursePaike()
    })
    return {
        filterTeacheroption,
        filterRoomOption,
        filterCourseOption,
        courseOptions,
        teacherOptions,
        roomOptions,
        courseOptionsPaike,
        getPaikeCourseList
    }


}