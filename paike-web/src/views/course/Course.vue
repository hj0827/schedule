<template>
    <a-form layout="inline" style="margin-bottom: 10px;">
        <a-form-item label="课程名称">
            <a-input v-model:value="listParm.courseName" placeholder="请输入课程名称"></a-input>
        </a-form-item>

        <a-button @click="searchBtn" class="margin-left">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" type="danger" class="margin-left">
            <template #icon>
                <reload-outlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:course:add']" @click="addBtn" type="primary" class="margin-left">
            <template #icon>
                <plus-outlined />
            </template>
            新增
        </a-button>
        <a-button v-permission="['sys:course:delete']" @click="deleteSelectedCourses" type="danger" class="margin-left">
            <template #icon>
                <delete-outlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <!-- 表格列表 -->
    <a-table  :dataSource="tableList.list" :columns="columns" :pagination="rolePage" bordered
        :scroll="{ y: tableHeight }" rowKey="courseId" :indentSize="15"
        :row-selection="{ selectedRowKeys: selectedCourseIds, onChange: onSelectChange, type: 'checkbox' }">
        <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'courseType'">
                <a-tag v-if="record.courseType === '0'" color="green">春季</a-tag>
                <a-tag v-if="record.courseType === '1'" color="blue">秋季</a-tag>
            </template>
            <template v-if="column.key === 'isMergeClasses'">
                <a-tag v-if="record.isMergeClasses === '0'" color="green">否</a-tag>
                <a-tag v-if="record.isMergeClasses === '1'" color="red">是</a-tag>
            </template>
            <template v-if="column.key === 'courseColor'">
                <div v-if="record.courseColor"
                    :style="{ height: '20px', width: '100%', backgroundColor: record.courseColor.substring(record.courseColor.indexOf('-') + 1, record.courseColor.length) }">
                </div>
            </template>
            <template v-if="column.key === 'action'">
                <a-button v-permission="['sys:course:edit']" @click="editBtn(record)" type="primary">
                    <template #icon>
                        <edit-outlined />
                    </template>
                    编辑
                </a-button>
                <a-button v-permission="['sys:course:delete']" @click="deleteBtn(record)" style="margin-left: 10px;"
                    type="danger">
                    <template #icon>
                        <delete-outlined />
                    </template>
                    删除
                </a-button>
            </template>
        </template>
    </a-table>
    <!-- 新增或编辑弹窗 -->
    <add-course @save="save" ref="addRef"></add-course>
</template>

<script lang="ts" setup>
import { DocumentTitle } from '@/type/BaseEnum'
import { onMounted } from 'vue';
import AddCourse from './AddCourse.vue';
import type { SelectProps } from 'ant-design-vue'
import { ref } from 'vue';
import useTable from '../../composable/course/useTable';
import useCourse from '../../composable/course/useCourse';

// 表格相关的操作
const { rolePage, tableList, columns, tableHeight, listParm, getList } = useTable()

const options = ref<SelectProps['options']>([
    { label: '春季', value: '0' },
    { label: '秋季', value: '1' },
])

// 新增、编辑
const { addBtn, editBtn, deleteBtn, save, addRef, searchBtn, resetBtn, deleteSelectedCourses, onSelectChange, selectedCourseIds } = useCourse(getList, listParm)

onMounted(() => {
    document.title = DocumentTitle.course;

    // 从 sessionStorage 中恢复搜索参数
    const savedParm = sessionStorage.getItem('courseSearchParm');
    if (savedParm) {
        Object.assign(listParm, JSON.parse(savedParm));
        getList();
    }
})
</script>

<style scoped lang="scss">
.select-width {
    width: 120px;
}

.margin-left {
    margin-left: 10px;
}
</style>