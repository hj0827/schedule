<template>
    <!-- 搜索栏 -->
    <a-form layout="inline" style="margin-bottom: 15px;">
        <a-form-item>
            <a-input v-model:value="listParm.name" placeholder="请输入姓名"></a-input>
        </a-form-item>
        <a-form-item>
            <a-input v-model:value="listParm.phone" placeholder="请输入电话"></a-input>
        </a-form-item>

        <a-button @click="searchBtn" style="margin-right: 10px;">
            <template #icon>
                <SearchOutlined />
            </template>
            搜索
        </a-button>
        <a-button @click="resetBtn" type="danger" style="margin-right: 10px;">
            <template #icon>
                <reload-outlined />
            </template>
            重置
        </a-button>
        <a-button v-permission="['sys:user:add']" type="primary" @click="addBtn" style="margin-right: 10px;">
            <template #icon>
                <PlusOutlined />
            </template>
            新增
        </a-button>
        <a-button v-permission="['sys:user:add']" type="primary" @click="importBtn" style="margin-right: 10px;"
            class="importBtn">
            <template #icon>
                <vertical-align-top-outlined />
            </template>
            导入
        </a-button>
        <a-button v-permission="['sys:user:delete']" type="danger" @click="deleteSelectedUsers"
            style="margin-right: 10px;">
            <template #icon>
                <delete-outlined />
            </template>
            删除选中
        </a-button>
    </a-form>
    <a-table :dataSource="tableList.list" :pagination="page" :columns="columns" :scroll="{ y: tableHeight }"
    :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, type: 'checkbox' }"
    :rowKey="(record: UserRecord) => record.userId" :onRow="onRow">
        <!-- 性别 -->
        <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'sex'">
                <a-tag color="green" v-if="record.sex == 0">男</a-tag>
                <a-tag color="red" v-if="record.sex == 1">女</a-tag>
            </template>
            <template v-if="column.dataIndex === 'action'">
                <a-button v-permission="['sys:user:edit']" @click="editBtn(record)" style="margin-right: 10px;"
                    type="primary">
                    <template #icon>
                        <edit-outlined />
                    </template>
                    编辑
                </a-button>
                <a-button v-permission="['sys:user:delete']" @click="deleteBtn(record)" type="danger">
                    <template #icon>
                        <delete-outlined />
                    </template>
                    删除
                </a-button>
            </template>
        </template>
    </a-table>
    <!-- 弹窗组件 -->
    <add-user ref="addRef" @refresh="refresh"></add-user>
    <import-user ref="importRef" @refresh="refresh"></import-user>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, defineComponent } from 'vue';
import { useRouter } from 'vue-router';
import ImportUser from './ImportUser.vue';
import AddUser from './AddUser.vue';
import useUser from '@/composable/user/useUser';
import useTable from '@/composable/user/useTable';
import { DocumentTitle } from '@/type/BaseEnum';

// 定义用户记录的类型
interface UserRecord {
    userId: number | string;
    name?: string;
    phone?: string;
    sex?: number;
    // 其他字段根据实际需求添加
}

const router = useRouter();
const { tableHeight, tableList, listParm, page, getList, columns, resetBtn, searchBtn, refresh, selectedRowKeys, onSelectChange } = useTable();
const { addBtn, editBtn, deleteBtn, save, addRef, importBtn, importRef, deleteSelectedUsers } = useUser(getList,selectedRowKeys);

// 处理行点击事件
const onRow = (record: any) => {
    return {
        onClick: () => {
            const index = selectedRowKeys.value.indexOf(record.userId);
            if (index > -1) {
                selectedRowKeys.value.splice(index, 1);
            } else {
                selectedRowKeys.value.push(record.userId);
            }
        }
    };
};

// 在组件挂载时恢复状态
onMounted(() => {
    document.title = DocumentTitle.user; // 设置页面标题
    const savedState = sessionStorage.getItem('searchState');
    if (savedState) {
        Object.assign(listParm, JSON.parse(savedState));
        getList();
    }
});
</script>

<style lang="scss" scoped>
.importBtn {
    background-color: #67C23A;
    border: 1px solid #fcfcfc;
}
</style>