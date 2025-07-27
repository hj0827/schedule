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
        <!-- 新增导出按钮 -->
        <a-button @click="exportTeacherData" type="primary" style="margin-right: 15px;">
            <template #icon>
                <file-excel-outlined />
            </template>
            导出教师信息
        </a-button>
    </a-form>
    <!-- 表格 -->
    <a-table :scroll="{ y: tableHeight, x: 1500 }" bordered :dataSource="tableList.list"
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
            <!-- 教师类型显示 -->
            <template v-if="column.key == 'teacherType'">
                <a-tag v-if="record.teacherType === '全职'" color="blue">全职</a-tag>
                <a-tag v-else-if="record.teacherType === '外聘'" color="green">外聘</a-tag>
                <span v-else>{{ record.teacherType }}</span>
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
import * as XLSX from 'xlsx';
import { FileExcelOutlined } from '@ant-design/icons-vue';

// 表格
const { listParm, rolePage, columns, tableList, getList, tableHeight, searchBtn, resetBtn } = useTable()
// 教师的新增、编辑
const { addBtn, editBtn, deleteBtn, addRef, save, deleteSelectedTeachers, onSelectChange, selectedTeacherIds } = useTeacher(getList)

// 导出教师数据
const exportTeacherData = () => {
    if (!tableList.list?.length) return;
    
    const filename = `教师信息_${new Date().toISOString().split('T')[0]}.xlsx`;
    
    // 准备导出数据
    const exportData = tableList.list.map(item => ({
        '教师姓名': item.teacherName,
        '教师编号': item.teacherNum,
        '备注': item.teacherDesc,
        '身份证号': item.idCard,
        '手机号码': item.phone,
        '开户行': item.bankName,
        '开户市': item.bankCity,
        '支行': item.bankBranch,
        '银行账号': item.bankAccount,
        '课酬标准': item.salaryStandard,
        '教师类型': item.teacherType
    }));
    
    // 创建工作簿和工作表
    const wb = XLSX.utils.book_new();
    const ws = XLSX.utils.json_to_sheet(exportData);
    
    // 设置列宽
    ws['!cols'] = [
        { wch: 15 }, // 教师姓名
        { wch: 15 }, // 教师编号
        { wch: 20 }, // 备注
        { wch: 20 }, // 身份证号
        { wch: 15 }, // 手机号码
        { wch: 20 }, // 开户行
        { wch: 15 }, // 开户市
        { wch: 20 }, // 支行
        { wch: 20 }, // 银行账号
        { wch: 10 }, // 课酬标准
        { wch: 10 }  // 教师类型
    ];
    
    // 添加工作表到工作簿
    XLSX.utils.book_append_sheet(wb, ws, '教师信息');
    
    // 导出文件
    XLSX.writeFile(wb, filename);
};

onMounted(() => {
    document.title = DocumentTitle.teacher; // 设置页面标题
    const savedteacher = sessionStorage.getItem('savedteacher');
    if (savedteacher) {
        Object.assign(listParm, JSON.parse(savedteacher));
        getList();
    }
});
</script>