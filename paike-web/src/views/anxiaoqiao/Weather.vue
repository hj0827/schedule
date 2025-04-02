<template>
    <div class="scheduling-tips">
        <div class="spin-container-total">
            <div class="scheduling-tips-title">
                <h2>排课小技巧</h2>
            </div>
            <div v-if="isLoading" class="spin-container-tip">
                <a-spin tip="">
                    <div class="spin-content"></div>
                </a-spin>
            </div>
        </div>
        <div v-if="isLoading" class="spin-container">
        </div>
        <ul v-else>
            <li v-for="(tip, index) in tips" :key="index" v-html="highlightedTip(tip)"></li>
        </ul>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import hljs from 'highlight.js';
import 'highlight.js/styles/default.css';
import { Spin } from 'ant-design-vue'; // 引入 Spin 组件

const tips = ref<string[]>([]);
const isLoading = ref(true); // 添加 isLoading 状态

const highlightedTip = computed(() => {
    return (tip: string) => {
        return hljs.highlightAuto(tip).value;
    };
});

async function fetchTips() {
    const apiKey = '65156760c45841b08ddd2a190707e3f4.3BGcKZHzBgfl2vpY';
    const apiUrl = 'https://open.bigmodel.cn/api/paas/v4/async/chat/completions';

    const messages = [
        {
            role: "user",
            content: [
                {
                    type: "text",
                    text: "三句话的排课小技巧。",
                },
            ],
        },
    ];

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiKey}`,
        },
        body: JSON.stringify({
            model: 'glm-4-plus',
            messages: messages,
        }),
    };

    try {
        const response = await fetch(apiUrl, requestOptions);
        const data = await response.json();
        const taskId = data.id;

        let taskStatus = '';
        let getCnt = 0;
        while (taskStatus !== 'SUCCESS' && taskStatus !== 'FAIL' && getCnt <= 40) {
            const resultResponse = await fetch(`https://open.bigmodel.cn/api/paas/v4/async-result/${taskId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${apiKey}`,
                },
            });
            const resultData = await resultResponse.json();
            taskStatus = resultData.task_status;
            if (taskStatus === 'SUCCESS') {
                const content = resultData.choices[0].message.content;
                console.log('原始内容:', content); // 调试信息

                // 分割内容为多个技巧，并显式声明 tip 的类型
                tips.value = content.split('\n').filter((tip: string) => tip.trim() !== '');
            } else if (taskStatus === 'FAIL') {
                console.error('任务处理失败');
            } else {
                await new Promise(resolve => setTimeout(resolve, 2000));
                getCnt++;
            }
        }
    } catch (error) {
        console.error('请求出错:', (error as Error).message);
    } finally {
        isLoading.value = false; // 请求完成后关闭加载动画
    }
}

onMounted(() => {
    fetchTips();
});
</script>

<style scoped>
.scheduling-tips {
    margin: 20px 0;
}

.scheduling-tips h2 {
    font-size: 18px;
    font-weight: bold;
}

.scheduling-tips ul {
    list-style-type: disc;
    padding-left: 20px;
}

.scheduling-tips li {
    margin-bottom: 10px;
}

.spin-container {
    position: relative;
    height: 100px;
    display: flex;
    justify-content: center;
    align-items: center;
}

.spin-container-total {
    display: flex;
    /* justify-content: center; */
    align-items: center;
}

.spin-container-tip{
    display: flex;
    margin: 0px 0px 10px 15px;
}
</style>