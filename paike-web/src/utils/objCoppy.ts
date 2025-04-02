// 对象快速复制
export default function objCoppy(obj1: any,obj2:any) {
    Object.keys(obj2).forEach(key =>{
        obj2[key] = obj1[key]
    })
}