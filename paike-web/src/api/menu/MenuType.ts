// 菜单数据类型
export interface MenuModel{
    editType: string,
    menuId: string,
    parentId: string,
    title:string,
    code:string,
    name:string,
    path:string,
    url:string,
    type:string,
    icon:string,
    parentName:string,
    orderNum:string
}

export interface TreeNode{
    parentId:string,
    parentName:string, 
}