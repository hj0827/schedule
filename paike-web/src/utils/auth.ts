 
//  存储token
export function getToken() {
  return sessionStorage.getItem('token')
}
export function setToken(token: string) {
  return sessionStorage.setItem('token', token)
}
// 存储用户id
export function setUserId(userId: string) {
  return sessionStorage.setItem('userId', userId)
}
export function getUserId() {
  return sessionStorage.getItem('userId')
}
export function removeUserId() {
  return sessionStorage.removeItem('userId')
}
// 存储用户名
export function setUserName(username: string) {
  return sessionStorage.setItem('username', username)
}
export function getUserName() {
  return sessionStorage.getItem('username')
}
export function removeUserName() {
  return sessionStorage.removeItem('username')
}
// name
export function setName(name: string) {
  return sessionStorage.setItem('name', name)
}
export function getName() {
  return sessionStorage.getItem('name')
}
export function removeName() {
  return sessionStorage.removeItem('name')
}

// sesionStorage 清空
export function clearSession() {
  return sessionStorage.clear()
}