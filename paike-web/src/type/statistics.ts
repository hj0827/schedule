// 课时统计数据类型
export interface ClassHourStatistics {
  id: string | number;
  teacherId: number;
  teacherName: string;
  courseId: number;
  courseName: string;
  duration: number;  // 课时长度（分钟）
  courseCount: number;
  dateTime: string;  // 课程日期
  beginTime: string; // 开始时间
}

// 课程详情类型
export interface CourseDetail {
  courseId: number;
  courseName: string;
  duration: number;
  dateTime: string;
  beginTime: string;
}

// 教师统计类型
export interface TeacherStat {
  id: string | number;
  teacherId: number;
  teacherName: string;
  courseId: number;
  courseName: string;
  duration: number;
  courses?: CourseDetail[];
}

// API响应类型
export interface ApiResponse<T> {
  code: number;
  msg: string;
  data: T;
}

// 课时统计查询参数
export interface StatisticsQueryParams {
  teacherId?: number;
  courseId?: number;
  startDate?: string;
  endDate?: string;
} 