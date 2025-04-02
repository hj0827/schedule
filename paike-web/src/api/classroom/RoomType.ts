export interface RoomModel {
    type:string,
    roomId:string,
    roomName:string,
    roomAddress:string
}

export interface ListParm  {
    pageSize: number,
    currentPage:number,
    roomName:string
}

