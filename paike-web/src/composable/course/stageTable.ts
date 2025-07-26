// 业务处理 将可以复用的业务放在一块
import { onMounted, reactive, ref } from "vue";
import { getStageListApi, editStageApi, deleteStageApi, addStageApi, type Stage } from '@/api/course/Stage'; // Import the API function and Stage interface

// 定义 API 返回的数据结构 (Assuming a structure like the Course API example)
interface ApiResponse<T> { // Use generic type for data
    code: number;
    data?: T; // Data can be of type T (e.g., Stage[])
    message?: string;
}

// Use a default export function similar to the course example
export default function useStageTable() {
    // Stage list data
    const stages = ref<Stage[]>([]);
    const loading = ref(false);
    const error = ref<Error | null>(null);

    // Define columns for the table, matching stage_table fields
    const columns = [

        {
            title: '阶段名称',
            dataIndex: 'stageName',
            key: 'stageName'
        },
        {
            title: '描述',
            dataIndex: 'description',
            key: 'description'
        },
        {
            title: '创建时间',
            dataIndex: 'createTime',
            key: 'createTime'
        },
        {
            title: '更新时间',
            dataIndex: 'updateTime',
            key: 'updateTime'
        },
        {
            title: '操作',
            align: 'center',
            key: 'action',
            width: 150
        },
    ];

    // Function to fetch stages
    const fetchStages = async () => {
        loading.value = true;
        error.value = null;
        try {
            // Use the imported API function
            const res = await getStageListApi() as ApiResponse<Stage[]>;

            if (res && res.code === 200 && res.data) {
                stages.value = res.data;
            } else {
                // Handle API errors or unexpected response structure
                 error.value = new Error(res.message || 'Failed to fetch stages');
                 console.error('API error fetching stages:', res.message);
                 stages.value = []; // Clear data on error
            }

        } catch (e: any) {
            error.value = e;
            console.error('Error fetching stages:', e);
             stages.value = []; // Clear data on error
        } finally {
            loading.value = false;
        }
    };

    // Function to edit a stage
    const editStage = async (stage: Stage) => {
        // You might want separate loading/error states for edit/delete
        // For simplicity, reusing the main loading/error for now
        // loading.value = true; // Consider separate loading state
        // error.value = null; // Consider separate error state
        try {
            const res = await editStageApi(stage) as ApiResponse<any>; // Assuming success response format

            if (res && res.code === 200) {
                console.log('Stage edited successfully:', res.message);
                // Refresh the list after successful edit
                fetchStages();
                return true; // Indicate success
            } else {
                 console.error('API error editing stage:', res.message);
                 // Handle specific edit error messages if needed
                 throw new Error(res.message || 'Failed to edit stage');
            }
        } catch (e: any) {
            console.error('Error editing stage:', e);
             throw e; // Re-throw to be caught by the component if needed
        } finally {
            // loading.value = false; // Consider separate loading state
        }
    };

     // Function to delete a stage
    const deleteStage = async (stageId: number) => {
        // loading.value = true; // Consider separate loading state
        // error.value = null; // Consider separate error state
        try {
            const res = await deleteStageApi(stageId) as ApiResponse<any>; // Assuming success response format

             if (res && res.code === 200) {
                console.log('Stage deleted successfully:', res.message);
                // Refresh the list after successful delete
                fetchStages();
                 return true; // Indicate success
            } else {
                 console.error('API error deleting stage:', res.message);
                 // Handle specific delete error messages if needed
                 throw new Error(res.message || 'Failed to delete stage');
            }
        } catch (e: any) {
            console.error('Error deleting stage:', e);
             throw e; // Re-throw to be caught by the component if needed
        } finally {
            // loading.value = false; // Consider separate loading state
        }
    };

    // === Add function to add a new stage ===
    const addStage = async (stage: { stageName: string; description: string | null }) => {
        // You might want a separate loading state for adding
        // const adding = ref(false); adding.value = true;
        try {
            const res = await addStageApi(stage) as ApiResponse<any>;

            if (res && res.code === 200) {
                 console.log('Stage added successfully:', res.message);
                // Refresh the list after successful addition
                fetchStages();
                 return true; // Indicate success
            } else {
                 console.error('API error adding stage:', res.message);
                 throw new Error(res.message || 'Failed to add stage');
            }
        } catch (e: any) {
             console.error('Error adding stage:', e);
             throw e; // Re-throw to be caught by the component if needed
        } finally {
             // adding.value = false;
        }
    };
    // ========================================

    // Fetch data when the composable is used (component is mounted)
    onMounted(() => {
        fetchStages();
    });

    return {
        stages,
        loading,
        error,
        columns, // Export columns as well
        fetchStages, // Export fetchStages if you need to manually refresh later
        editStage,   // Export edit function
        deleteStage, // Export delete function
        addStage,    // Export add function
    };
}