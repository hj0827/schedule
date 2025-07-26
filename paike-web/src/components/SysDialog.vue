<template>
    <a-modal :visible="props.visible" destroyOnClose="true" :width="props.width" :title="props.title" @cancel="onClose" :maskClosable="false" :style="{ top: props.top + 'px' }">
      <template #footer>
        <template v-if="props.mode === 'edit'">
          <a-button type="danger" @click="onDelete">删除</a-button>
          <a-button type="primary" @click="onConfirm">修改</a-button>
        </template>
        <template v-else>
          <a-button @click="onClose">取消</a-button>
          <a-button type="primary" @click="onConfirm">确定</a-button>
        </template>
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
        height:number;
        mode?:string;
        top?:number;
    }>(),{
        title:'标题',
        visible:false,
        width:650,
        height:250,
        mode:'add',
        top:100
    })

    // 父组件调用子组件的方法 defineEmits
    const emit = defineEmits(['onClose','onConfirm', 'onDelete'])
    // 关闭弹窗
    const onClose = () => {
        emit('onClose')
    };
    
    // 弹窗确定
    const onConfirm =()=>{
        emit('onConfirm')
    }

    // 删除
    const onDelete = () => {
        emit('onDelete')
    }
</script>