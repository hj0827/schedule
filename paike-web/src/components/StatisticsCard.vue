<template>
  <a-card :title="title" :bordered="false" class="statistics-card">
    <template #extra>
      <a-tag :color="color">{{ total }}</a-tag>
    </template>
    <a-list :data-source="items" size="small">
      <template #renderItem="{ item }">
        <a-list-item>
          <span>{{ item.name }}</span>
          <span>{{ item.value }} 课时</span>
        </a-list-item>
      </template>
    </a-list>
  </a-card>
</template>

<script lang="ts" setup>
interface StatisticsItem {
  name: string;
  value: number;
}

interface Props {
  title: string;
  items: StatisticsItem[];
  total: number;
  color?: string;
}

withDefaults(defineProps<Props>(), {
  color: '#1890ff'
});
</script>

<style scoped>
.statistics-card {
  margin-bottom: 16px;
}

.ant-list-item {
  justify-content: space-between;
}
</style> 