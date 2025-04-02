<template>
    <div class="container">
        <!-- <h1>安小桥°</h1> -->
        <div id="response" class="conversation">
            <div v-if="showGreeting" class="greeting">
                您好，{{ name }}
            </div>
            <div v-if="showGreeting" class="">欢迎使用安小桥°，我是您的智能助手，有什么问题可以问我哦。</div>
            <!-- 排课小技巧 -->
            <Weather v-if="showGreeting"/>
            <div v-for="(message, index) in conversation" :key="index" :class="message.role">
                <div v-html="highlightedMessage(message.content)"></div>
            </div>
        </div>
        <div class="spin-container">
            <a-spin v-if="isLoading" tip="">
                <div class="spin-content"></div>
            </a-spin>
        </div>
    </div>

    <div class="input-group">
        <textarea v-model="input" placeholder="请输入你的问题" @keydown.enter="sendAsyncRequest"></textarea>
        <button @click="sendAsyncRequest">发送</button>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import hljs from 'highlight.js';
import 'highlight.js/styles/default.css';
import { message } from 'ant-design-vue'; // 引入 message 组件
import Weather from '@/views/anxiaoqiao/Weather.vue'; // 导入 Weather 组件

const input = ref('');
const response = ref('');
const isLoading = ref(false);
const conversation = ref<{ role: string, content: string }[]>([]);
const name = ref('');
const showGreeting = ref(true);

const highlightedMessage = computed(() => {
    return (content: string) => {
        return hljs.highlightAuto(content).value;
    };
});

onMounted(() => {
    // 从 session 中获取 name 值
    name.value = sessionStorage.getItem('name') || 'Guest';
});

async function sendAsyncRequest() {
    if (!input.value.trim()) {
        // 输入框为空时，弹出错误弹窗
        message.error('不能发送空内容');
        return;
    }

    isLoading.value = true;
    response.value = '';
    const apiKey = '65156760c45841b08ddd2a190707e3f4.3BGcKZHzBgfl2vpY';
    const asyncApiUrl = 'https://open.bigmodel.cn/api/paas/v4/async/chat/completions';
    const resultApiUrl = 'https://open.bigmodel.cn/api/paas/v4/async-result/';

    const messages = [
        {
            "role": "user",
            "content": [
                {
                    "type": "text",
                    "text": input.value
                }
            ]
        }
    ];

    // 添加用户输入到对话记录
    conversation.value.push({ role: 'user', content: input.value });

    const requestOptions = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${apiKey}`
        },
        body: JSON.stringify({
            model: 'glm-4-plus',
            messages: messages
        })
    };

    try {
        // 发送异步请求
        const asyncResponse = await fetch(asyncApiUrl, requestOptions);
        const asyncData = await asyncResponse.json();
        const taskId = asyncData.id;

        // 轮询任务结果
        let taskStatus = '';
        let getCnt = 0;
        while (taskStatus !== 'SUCCESS' && taskStatus !== 'FAIL' && getCnt <= 40) {
            const resultResponse = await fetch(`${resultApiUrl}${taskId}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${apiKey}`
                }
            });
            const resultData = await resultResponse.json();
            taskStatus = resultData.task_status;
            if (taskStatus === 'SUCCESS') {
                const content = resultData.choices[0].message.content;
                console.log('原始内容:', content); // 调试信息

                // 替换“智谱清言”为“安小桥°”
                let modifiedContent = content.replace(/我是人工智能助手智谱清言（ChatGLM），是基于智谱 AI 公司于 2024 年训练的语言模型开发的。/g, '我是安桥排课系统人工智能助手安小桥°，可以帮助您排课，');
                modifiedContent = modifiedContent.replace(/智谱清言/g, '安小桥°');
                modifiedContent = modifiedContent.replace(/智谱清言（ChatGLM）/g, '安小桥°');
                modifiedContent = modifiedContent.replace(/智谱/g, '**');

                // 在句子结束处插入换行符
                modifiedContent = modifiedContent.replace(/([。！？])\s*/g, '$1\n');

                console.log('替换后内容:', modifiedContent); // 调试信息

                response.value = modifiedContent;

                // 添加AI回答到对话记录
                conversation.value.push({ role: 'assistant', content: modifiedContent });
            } else if (taskStatus === 'FAIL') {
                response.value = '任务处理失败';

                // 添加错误信息到对话记录
                conversation.value.push({ role: 'assistant', content: '任务处理失败' });
            } else {
                await new Promise(resolve => setTimeout(resolve, 2000));
                getCnt++;
            }
        }
    } catch (error) {
        const errorMessage = (error as Error).message || '未知错误';
        response.value = `请求出错: ${errorMessage}`;

        // 添加错误信息到对话记录
        conversation.value.push({ role: 'assistant', content: `请求出错: ${errorMessage}` });
    } finally {
        isLoading.value = false;
        // 清空输入框
        input.value = '';
        // 隐藏问候语
        showGreeting.value = false;
    }
}
</script>

<style scoped>
/* 样式部分保持不变 */
.container {
    display: flex;
    flex-direction: column;
    max-height: 700px;
    height: 95%;
    overflow: auto;
    padding: 0px 200px 10px 200px;
}

.input-group {
    display: flex;
    height: 50px;
    margin-top: auto;
    margin-right: 200px;
}

textarea {
    width: 100%;
    border: 2px solid #b9b9b9;
    transition: border-color 0.3s ease;
    padding: 10px;
    border-radius: 5px;
    resize: none;
    margin-left: 200px;
    font-size: 14px;
}

textarea:focus {
    border-color: #929292;
    outline: none;
}

.spin-container {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 1000;
}

.conversation {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
    display: flex;
    flex-direction: column;
}

.conversation .user,
.conversation .assistant {
    white-space: pre-wrap;
    /* 保留空白符序列，并且文本会在必要时自动换行 */
}

.conversation .user {
    background-color: #e6ffe6;
    margin: 5px 0;
    padding: 10px;
    border-radius: 10px;
    max-width: 70%;
    align-self: flex-end;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    font-size: 14px;
    /* 添加字体大小 */
}

.conversation .assistant {
    background-color: #f9f9f9;
    /* 更浅的背景色 */
    margin: 10px 0;
    /* 增加外边距 */
    padding: 15px;
    /* 增加内边距 */
    border-radius: 15px;
    /* 圆角变大 */
    max-width: 70%;
    align-self: flex-start;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    /* 增强阴影效果 */
    font-size: 16px;
    /* 字体稍大 */
    color: #333;
    /* 字体颜色更深 */
    border: 1px solid #ddd;
    /* 添加边框 */
}

.greeting {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100px;
    /* 根据需要调整高度 */
    font-weight: bold;
    font-size: 20px;
    /* 根据需要调整字体大小 */
    text-align: center;
}

.input-group {
    display: flex;
    height: 50px; /* 保持高度 */
    margin-top: auto;
    margin-right: 200px;
    overflow: hidden; /* 取消滚动条 */
}

textarea {
    width: calc(100% - 80px); /* 减少宽度以适应按钮 */
    height: 100%; /* 使高度与 .input-group 一致 */
    border: none; /* 去掉边框 */
    background-color: transparent; /* 背景透明 */
    transition: background-color 0.3s ease; /* 过渡效果 */
    padding: 15px; /* 增加内边距 */
    border-radius: 30px 0 0 30px; /* 左侧圆角 */
    resize: none;
    font-size: 16px; /* 增加字体大小 */
    outline: none; /* 去掉聚焦时的默认边框 */
    overflow: hidden;
    background-color: #e9e9e9;
    line-height: 100%;
}

textarea:focus {
    background-color: #dbdbdb; /* 聚焦时的背景颜色 */
}

button {
    width: 70px; /* 固定宽度 */
    height: 100%; /* 使高度与 .input-group 一致 */
    border: none; /* 去掉边框 */
    background-color: #1890ff; /* 蓝色背景 */
    color: white; /* 白色文字 */
    font-size: 16px; /* 字体大小 */
    border-radius: 0 30px 30px 0; /* 右侧圆角 */
    cursor: pointer; /* 鼠标悬停时显示为手型 */
    transition: background-color 0.3s ease; /* 过渡效果 */
}

button:hover {
    background-color: #40a9ff; /* 悬停时的背景颜色 */
}
</style>