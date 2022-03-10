# Nginx
## 概述
Nginx是一个高性能的HTTP和反向代理服务器，特点是占有内存少，并发能力强
### 反向代理
#### 正向代理
在客户端(浏览器)配置代理服务器，通过代理服务器进行互联网访问
#### 反向代理 
反向代理，其实客户端对代理是无知的，因为客户端不需要任何配置就可以访问，只需要将请求发送到反向代理服务器，由反向代理服务器去选择目标服务器获取数据后，在返回给客户端，此时反向代理服务器和目标服务器对外就是一个服务器，暴露的是代理服务器地址，隐藏了真实的服务器IP地址
### 负载均衡
### 动静分离
为了加快网站的解析速度，可以把动态页面和静态页面由不同的服务器来解析，加快解析的速度。降低单个服务器的压力。
## 安装
### 安装pcre
- 联网下载pcre压缩文件依赖
```bash
wget http://downloads.sourceforge.net/project/pcre/pcre/8.37/pcre-8.37.tar.gz
```
- 解压压缩包

```bash
tar -xvf pcre-83.7.tar.gz
```
- 进入安装目录，使用`./configure`配置环境
- 使用`make install`命令安装依赖

这一步可能会提示没有安装gcc，使用`yum install -y gcc gcc-c++`安装

- `pcre-config --version`查看版本号

### 安装openssl,zlib
```bash
yum -y install make zlib zlib-devel gcc-c++ libtool openssl openssl-devel
```
### 安装nginx
安装成功后,usr目录下新增local/nginx,nginx目录下存放启动sbin
> 执行

```bash
./nignx
```
> 开放80端口

`firewall-cmd --list-all`查看开放端口
`sudo firewall-cmd --add-port=80/tcp --permanent`开放80端口
`firewall-cmd reload`重启防火墙
`ps -ef | grep nginx`查看后台
![](./images/nginx.jpg)
## nignx常用命令
> 查看版本

`./nginx -v`
> 启动nginx

`./nginx`
> 关闭nginx

`./nginx -s stop`

> 重新加载nginx

`./nignx -s reload`

## nginx配置文件
