<template>
    <a-modal :visible="props.visible" destroyOnClose="true" :width="props.width" :title="props.title" @cancel="onClose" :maskClosable="false">
      <template #footer>
        <a-button type="danger" @click="onClose">取消</a-button>
        <a-button type="primary" @click="onConfirm">确定</a-button>
      </template>
      <!-- 内容 -->
       <div :style="{height:props.height+'px'}" style="overflow-y: auto;overflow-x: initial;">
        <slot name="content"></slot>
       </div>
    </a-modal>
</template>

<script lang="ts" setup>
  import { ref } from 'vue'
    // 子组件接收父组件的值 defineProps
    const props = withDefaults(defineProps<{
        title?:string;
        visible?:boolean;
        width:number;
        height:number
    }>(),{
        title:'标题',
        visible:false,
        width:650,
        height:250
    })

    // 父组件调用子组件的方法 defineEmits
    const emit = defineEmits(['onClose','onConfirm'])
    // 关闭弹窗
    const onClose = () => {
        emit('onClose')
    };
    
    // 弹窗确定
    const onConfirm =()=>{
        emit('onConfirm')
    }
</script>