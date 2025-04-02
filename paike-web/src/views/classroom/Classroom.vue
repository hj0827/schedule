<template>
    <!-- 搜索栏 -->
    <a-form layout="inline" style="margin-bottom: 14px;">
        <a-form-item>
            <a-input v-model:value="listParm.roomName" placeholder="请输入教室名称"></a-input>
        </a-form-item>
        <a-button @click="searchBtn" style="margin-right: 15px;">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" style="margin-right: 15px;" type="danger">
            <template #icon>
                <ReloadOutlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:classroom:add']" @click="addBtn" type="primary" style="margin-right: 15px;">
            <template #icon>
                <PlusOutlined />
            </template>
            新增
        </a-button>
        <a-button v-permission="['sys:classroom:delete']" @click="deleteSelectedRooms" type="danger" style="margin-right: 15px;">
            <template #icon>
                <DeleteOutlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <!-- 表格 -->
    <a-table :dataSource="tableList.list" :columns="columns" :pagination="rolePage" :scroll="{ y: tableHeight }"
        bordered :row-selection="{ selectedRowKeys: selectedRoomIds, onChange: onSelectChange, type: 'checkbox' }"
        rowKey="roomId">
        <!-- 操作按钮 -->
        <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'action'">
                <a-button v-permission="['sys:classroom:edit']" type="primary" @click.stop="editBtn(record)">
                    <template #icon>
                        <EditOutlined />
                    </template>
                    <span>编辑</span>
                </a-button>
                <a-button v-permission="['sys:classroom:delete']" type="danger" style="margin-left: 10px;" @click.stop="deleteBtn(record)">
                    <template #icon>
                        <DeleteOutlined />
                    </template>
                    <span>删除</span>
                </a-button>
            </template>
        </template>
    </a-table>

    <!-- 弹窗组件 -->
    <Add ref="addRef" @save="save"></Add>
</template>

<script lang="ts" setup>
import { DocumentTitle } from '@/type/BaseEnum'
import { onMounted } from 'vue'
import Add from './Add.vue';
import useRoom from '../../composable/classroom/useRoom';
import useTable from '../../composable/classroom/useTable';

// 表格属性
const { tableHeight, tableList, columns, rolePage, listParm, getList, searchBtn, resetBtn } = useTable()

// 教室的新增、编辑
const { addBtn, editBtn, deleteBtn, addRef, save, deleteSelectedRooms, onSelectChange, selectedRoomIds } = useRoom(getList)

onMounted(() => {
    document.title = DocumentTitle.ClassRoom; // 设置页面标题
    const classroomSearchParm = sessionStorage.getItem('classroomSearchParm');
    if (classroomSearchParm) {
        Object.assign(listParm, JSON.parse(classroomSearchParm));
        getList();
    }
})
</script>