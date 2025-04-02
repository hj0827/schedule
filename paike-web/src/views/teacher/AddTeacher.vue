<!-- AddTeacher.vue -->
<template>
  <sys-dialog :title="dialog.title" :width="dialog.width" :height="dialog.height" :visible="dialog.visible"
    @onClose="onClose" @onConfirm="onConfirm">
    <template #content>
      <a-form labelAlign="right">
        <a-form-item v-bind="validateInfos.teacherName" :labelCol="{ style: 'width:80px;' }" label="教师名称">
          <a-input v-model:value="addModel.teacherName" placeholder="请输入教师名称"></a-input>
        </a-form-item>
        <a-form-item v-bind="validateInfos.teacherNum" :labelCol="{ style: 'width:80px;' }" label="教师编号">
          <a-input v-model:value="addModel.teacherNum" placeholder="请输入教师编号"></a-input>
        </a-form-item>
        <a-form-item :labelCol="{ style: 'width:80px;' }" label="教师描述">
          <a-input v-model:value="addModel.teacherDesc" placeholder="请输入教师描述"></a-input>
        </a-form-item>
      </a-form>

    </template>
  </sys-dialog>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import useDialog from '../../hooks/useDialog';
import SysDialog from '../../components/SysDialog.vue';
import { EditType, Title } from '../../type/BaseEnum';
import { TeacherModel } from '../../api/teacher/TeacherType';
import useForm from 'ant-design-vue/es/form/useForm';
import useInstance from '../../hooks/useInstance';
// 获取全局属性
const {global} = useInstance()

// 定义表单数据对象
const addModel = reactive<TeacherModel>({
  type: '',
  teacherId: '',
  teacherName: '',
  teacherNum: '',
  teacherDesc: ''
})
// 表单验证规则
const rules = reactive({
  teacherName: [{
    required: true,
    message: '请输入教师名称',
    trigger: 'change'
  }],
  teacherNum: [{
    required: true,
    message: '请输入教师编号',
    trigger: 'change'
  }]
})

// 获取表单相关的属性
const {validate,resetFields,validateInfos} = useForm(addModel,rules)

//注册事件
const emit = defineEmits(['save']) 
// 弹窗确定
const onConfirm = () => {
  validate().then(()=>{
    emit('save',addModel)
    onClose()
  })

}


//   弹窗属性
const { dialog, onClose, onShow } = useDialog();

// 弹窗显示
const show = (type: string,row:TeacherModel) => {
  // 清空表单
  resetFields()
  dialog.height = 200
  if (type === EditType.ADD) {
    dialog.title = Title.ADD
  } else {
    dialog.title = Title.EDIT 
    // 设置页面回显的数据
    global.$objCoppy(row,addModel)
  }
  addModel.type = type
  onShow()
}

// 暴露出去给外部组件使用
defineExpose({
  show
})


</script>