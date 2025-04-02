declare module 'xlsx';
declare module '*.xlsx' {
    const content: string;
    export default content;
  }