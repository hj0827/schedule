<template>
    <!-- 搜索栏 -->
    <a-form layout="inline" style="margin-bottom: 14px;">
        <a-form-item label="教师名称">
            <a-input v-model:value="listParm.teacherName" placeholder="请输入教师名称" />
        </a-form-item>
        <!-- 按钮 -->
        <a-button @click="searchBtn" style="margin-right: 15px;">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" type="danger" style="margin-right: 15px;">
            <template #icon>
                <reload-outlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:teacher:add']" @click="addBtn" type="primary" style="margin-right: 15px;">
            <template #icon>
                <plus-outlined />
            </template>
            新增
        </a-button>
        <a-button v-permission="['sys:teacher:delete']" @click="deleteSelectedTeachers" type="danger" style="margin-right: 15px;">
            <template #icon>
                <delete-outlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <!-- 表格 -->
    <a-table :scroll="{ y: tableHeight }" bordered :dataSource="tableList.list"
        :pagination="rolePage" :columns="columns"
        :row-selection="{ selectedRowKeys: selectedTeacherIds, onChange: onSelectChange, type: 'checkbox' }"
        rowKey="teacherId">
        <template #bodyCell="{ column, record }">
            <!-- 操作栏 -->
            <template v-if="column.key == 'action'">
                <a-button v-permission="['sys:teacher:edit']" @click="editBtn(record)" style="margin-right: 15px;" type="primary">
                    <template #icon>
                        <edit-outlined />
                    </template>
                    编辑
                </a-button>
                <a-button v-permission="['sys:teacher:delete']" @click="deleteBtn(record)" type="danger">
                    <template #icon>
                        <delete-outlined />
                    </template>
                    删除
                </a-button>
            </template>
        </template>
    </a-table>
    <!-- 新增、编辑 -->
    <add-teacher ref="addRef" @save="save"></add-teacher>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import AddTeacher from './AddTeacher.vue';
import useTeacher from '../../composable/teacher/useTeacher';
import useTable from '../../composable/teacher/useTable';
import { DocumentTitle } from '@/type/BaseEnum';

// 表格
const { listParm, rolePage, columns, tableList, getList, tableHeight, searchBtn, resetBtn } = useTable()
// 教师的新增、编辑
const { addBtn, editBtn, deleteBtn, addRef, save, deleteSelectedTeachers, onSelectChange, selectedTeacherIds } = useTeacher(getList)

onMounted(() => {
    document.title = DocumentTitle.teacher; // 设置页面标题
    const savedteacher = sessionStorage.getItem('savedteacher');
    if (savedteacher) {
        Object.assign(listParm, JSON.parse(savedteacher));
        getList();
    }
});
</script>