<template>
    <sys-dialog title="导入用户名单" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
        @onConfirm="onConfirm" @onClose="onClose">
        <template #content>
            <!-- <a-upload v-model:file-list="fileList" :before-upload="beforeUpload">
                <a-button>
                    <upload-outlined></upload-outlined>
                    Upload
                </a-button>
            </a-upload> -->

            <!-- <a-upload v-model:file-list="fileList" :before-upload="beforeUpload" @change="handleChange">
                <div class="avatar-uploader">
                    <div class="ant-upload-text">
                        <plus-outlined></plus-outlined>
                        <span>Upload</span>
                    </div>
                </div>
            </a-upload> -->

            <!-- 添加下载 -->
            <!-- 添加下载 -->
            <div class="downloadBtn">
                <div>模板：</div>
                <div>
                    <a :href="templatePath" download="ImportUserData.xlsx">
                        <a-button type="primary">
                            <vertical-align-bottom-outlined />
                            下载模板
                        </a-button>
                    </a>
                </div>
            </div>
            <div class="downColor">
                说明：<span>请严格按模板填写，否则可能导致无法准确导入。</span> 
            </div>

            <a-upload-dragger v-model:file-list="fileList" :before-upload="beforeUpload" @change="handleChange">
                <p class="ant-upload-drag-icon">
                    <inbox-outlined></inbox-outlined>
                </p>
                <p class="ant-upload-text">点击或者拖拽到该区域上传</p>
            </a-upload-dragger>
        </template>
    </sys-dialog>
</template>

<script setup lang="ts">
import { UploadOutlined } from '@ant-design/icons-vue';
import { defineComponent, ref } from 'vue';
import type { UploadChangeParam, UploadProps } from 'ant-design-vue';

import SysDialog from '@/components/SysDialog.vue';
import useDialog from '@/hooks/useDialog';
import { importUserApi } from '@/api/user/user';
import { message } from 'ant-design-vue'; // 重新引入 message
import * as XLSX from 'xlsx'; // 引入 xlsx 库
import { Title } from '@/type/BaseEnum';

const fileList = ref<UploadProps['fileList']>([]);
let selectedFile: File | null = null;

const handleChange = ({ file, fileList }: UploadChangeParam) => {
    if (file.status === 'removed') {
        selectedFile = null; // 清空 selectedFile
    } else if (file.status !== 'uploading') {
        console.log(file, fileList);
    }
};

const beforeUpload = (file: File) => {
    selectedFile = file;
    return false; // 阻止默认上传行为
};

// 弹窗属性
const { dialog, onClose, onShow } = useDialog();
// 显示弹框
const show = async (type: string, row: any) => {
    onShow()
}

defineExpose({
    show
})

// 定义 emit
const emit = defineEmits(['refresh']);

// 弹框确认
const onConfirm = async () => {
    if (!selectedFile) {
        message.warning('请选择一个文件');
        return;
    }

    const reader = new FileReader();
    reader.onload = async (e) => {
        const data = e.target?.result;
        if (data instanceof ArrayBuffer) {
            const workbook = XLSX.read(new Uint8Array(data), { type: 'array' });
            const sheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[sheetName];
            const json = XLSX.utils.sheet_to_json(worksheet);

            // 数据转换
            // 数据转换
            const transformedData = json.map((item: any) => ({
                username: item.账户,
                password: item.密码,
                name: item.姓名,
                roleId: item["角色【系统管理员：1，普通管理员：2，任课教师：3，助学老师&学生：4】"],
                __rowNum__: item.__rowNum__
            }));

            try {
                const response = await importUserApi(transformedData);
                message.success('用户导入成功');
                console.log('API Response:', response);
                emit('refresh')
            } catch (error: any) {
                const errorMessage = error.response ? error.response.data.msg : error.message;
                const existingUsers = error.response ? error.response.data.data : [];
                // message.error(`用户导入失败: ${errorMessage}`);
                // console.error('API Error:', error);

                // 可以在这里处理 existingUsers，例如显示具体存在的用户
                if (existingUsers.length > 0) {
                    console.log('Existing Users:', existingUsers);
                    // 进一步处理 existingUsers，例如显示在弹窗中
                }
            }
        }
    };
    reader.readAsArrayBuffer(selectedFile);

    onClose();
};

// 定义模板路径
const templatePath = '/src/assets/ExcelTemplates/ImportUserData.xlsx';
</script>

<style lang="scss" scoped>
// 深度
::v-deep {
    .ant-upload {
        width: 300px;
        height: 150px;
    }
}

.downloadBtn {
    margin-bottom: 8px;
    display: flex;
    align-items: center;
}

.downColor{
    margin-bottom: 8px;
}

.downColor>span{
    color: red;
}
</style>