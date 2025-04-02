<template>
    <!-- 搜索栏 -->
    <a-form layout="inline" style="margin-bottom: 10px;">
        <a-form-item>
            <a-input v-model:value="listParm.roleName" placeholder="请输入角色名称"></a-input>
        </a-form-item>
        <a-button @click="searchBtn" style="margin-right: 15px;">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" style="margin-right: 15px;" type="danger">
            <template #icon>
                <reload-outlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:role:add']" @click="addBtn" type="primary" style="margin-right: 15px;">
            <template #icon>
                <plus-outlined />
            </template>
            新增
        </a-button>
        <a-button v-permission="['sys:role:delete']" @click="deleteSelectedRoles" type="danger" style="margin-right: 15px;">
            <template #icon>
                <delete-outlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <!-- 表格操作 -->
    <a-table :dataSource="tableList.list" :pagination="rolePage" :columns="columns" :scroll="{ y: tableHeight }"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: 'checkbox' }"
        :rowKey="(record: RoleType) => record.roleId">
        <template #bodyCell="{ column, record }">
            <template v-if="column.dataIndex === 'action'">
                <a-button v-permission="['sys:role:edit']" @click="editBtn(record)" style="margin-right: 10px;" type="primary">
                    <template #icon>
                        <edit-outlined />
                    </template>
                    编辑
                </a-button>
                <a-button v-permission="['sys:role:permission']" @click="assignBtn(record)" style="margin-right: 10px;" type="primary">
                    <template #icon>
                        <lock-outlined />
                    </template>
                    分配权限
                </a-button>
                <a-button v-permission="['sys:role:delete']" @click="deleteBtn(record)" type="danger">
                    <template #icon>
                        <delete-outlined />
                    </template>
                    删除
                </a-button>
            </template>
        </template>
    </a-table>
    <!-- 新增弹窗 -->
    <add-role ref="addRef" @save="save"></add-role>
    <!-- 分配权限 -->
    <assign-role ref="assignRef"></assign-role>
</template>

<script setup lang="ts">
import { DocumentTitle } from '@/type/BaseEnum'
import { onMounted, ref } from 'vue';
import useRole from '@/composable/role/useRole';
import AddRole from './AddRole.vue';
import useTable from '@/composable/role/useTable';
import AssignRole from './AssignRole.vue';

// 表格操作
const { tableHeight, tableList, columns, listParm, rolePage, getList, resetBtn, searchBtn } = useTable()

// 选中的行键
const selectedRowKeys = ref<number[]>([]);
const onSelectChange = (selectedKeys: number[]) => {
    selectedRowKeys.value = selectedKeys;
};

// 角色操作
const { addBtn, editBtn, deleteBtn, save, addRef, assignRef, assignBtn, deleteSelectedRoles } = useRole(getList, selectedRowKeys)

onMounted(() => {
    document.title = DocumentTitle.role; // 设置页面标题
})
</script>

<style scoped lang="scss">
.select-width {
    width: 120px;
}

.margin-left {
    margin-left: 10px;
}

:deep(.ant-table-pagination) {
    margin: 5px;
}

.ant-table-pagination {
    margin: 5px;
}
</style>