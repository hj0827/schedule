<template>
    <LoadingOverlay :visible="loading" />
    <a-form layout="inline" style="margin-bottom: 15px;">
        <!-- 教室 -->
        <a-form-item label="教室">
            <a-select v-model:value="listParm.roomId" show-search placeholder="请选择教室" style="width: 200px;"
                :options="roomOptions" :filter-option="filterRoomOption" allowClear>
            </a-select>
        </a-form-item>
        <!-- 课程 -->
        <a-form-item label="课程">
            <a-select v-model:value="listParm.courseId" show-search placeholder="请选择课程" style="width: 200px;"
                :options="courseOptions" :filter-option="filterCourseOption" allowClear>
            </a-select>
        </a-form-item>
        <!-- 教师 -->
        <a-form-item label="教师">
            <a-select v-model:value="listParm.teacherId" show-search placeholder="请选择教师" style="width: 200px;"
                :options="teacherOptions" :filter-option="filterTeacheroption" allowClear>
            </a-select>
        </a-form-item>
        <!-- 考期 -->
        <!--  -->
        <a-button style="margin-right: 10px;" @click="searchBtn">
            <template #icon>
                <search-outlined />
            </template>
            搜索
        </a-button>
        <a-button v-permission="['sys:schedule:add']" @click="courseBtn" type="primary">
            <template #icon>
                <plus-outlined />
            </template>
            排课
        </a-button>
    </a-form>
    <!-- 日历 -->
    <calendar-list ref="refList"></calendar-list>
    <!-- 排课界面 -->
    <add-schedule ref="addCourse" @refreshList="refreshList"></add-schedule>
</template>

<script setup lang="ts">
import {DocumentTitle} from '@/type/BaseEnum'
import { onMounted } from 'vue';
import LoadingOverlay from '@/components/loadingOverlay.vue';
import CalendarList from './CalendarList.vue';
import { ref, reactive } from 'vue';
import AddSchedule from './AddSchedule.vue';
import useselect from '../../composable/schedule/useSelect';
import useTable from '@/composable/schedule/useTable';

const { rolePage, tableList, columns, tableHeight, listParm, getList, loading } = useTable();
const refList = ref();
// 下拉属性
const { roomOptions, teacherOptions, courseOptions, courseOptionsPaike, filterCourseOption, filterRoomOption, filterTeacheroption } = useselect(getList, listParm);

// 排课界面ref属性
const addCourse = ref<{ show: () => void }>();

// 排课按钮
const courseBtn = () => {
    addCourse.value?.show();
};

// 刷新列表
const refreshList = () => {
    console.log('刷新列表');
    refList.value.getScheduleList(listParm);
};

// 搜索按钮
const searchBtn = () => {
    refList.value.getScheduleList(listParm);
};

onMounted(() => {
    document.title = DocumentTitle.schedule;
})
</script>