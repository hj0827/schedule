// 业务处理
import { ListParm, CourseModel } from "./BaseCourse"
import http from "../../http"

// Define the Stage interface again for clarity, although it might be in another shared type file
export interface Stage {
  id: number;
  stageName: string; // Assuming backend uses stageName (驼峰命名) based on CourseController example
  description: string | null;
  createTime: string; // Assuming datetime is returned as a string
  updateTime: string; // Assuming datetime is returned as a string
}

// Add the API function to get the list of stages
export const getStageListApi = () => {
    return http.get({
        url: '/api/stages' // This matches your backend controller endpoint
    });
};

// Add the API function to edit a stage
export const editStageApi = (stage: Stage) => {
    return http.put({ // Assuming PUT method for update
        url: `/api/stages`, // Or PUT /api/stages/{id} depending on backend
        data: stage // Send the updated stage object in the request body
    });
};

// Add the API function to delete a stage
export const deleteStageApi = (stageId: number) => {
    return http.delete({ // Assuming DELETE method
        url: `/api/stages/${stageId}` // Send the ID in the path
    });
};

// === Add the API function to add a new stage ===
export const addStageApi = (stage: { stageName: string; description: string | null }) => {
    return http.post({ // Assuming POST method for creation
        url: `/api/stages`, // Assuming POST /api/stages endpoint
        data: stage // Send the new stage data in the request body
    });
};