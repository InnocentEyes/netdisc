
## netdisc

基于Jwt+Nginx+SpringBoot+(Netty+WebSocket)+Redis+Mysql+FastDFS实现的云盘加即时好友聊天，文件分享的系统。


### 登录接口

`/login`,需要以json格式的数据格式,Post方式上传至接口,如下:

```
{"account": "15322255046","password": "qzl200919yya"}
```

登录成功后,返回以下值:
```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "avatarUrl": null,
        "account": "15322255046",
        "password": "cXpsMjAwOTE5eXlh",
        "createAt": "2022-02-17 05:36:39",
        "version": null,
        "userid": 1,
        "nickname": "qzlzzz",
        "username": "邱泽林"
    }
}
```
登录失败,返回以下值:

```
{
    "code": 500213,
    "msg": "密码错误",
}
```

### 注册接口

`/register`: 需要以json格式的数据格式,Post请求方式上传至接口,如下:

```
{"nickname": "qzxxxx","username": "邱泽鑫","account": "15875616710","password": "123456"}
```
- **nickname: 指的是用户的昵称**
- **username: 指的是用户的真实姓名**
- **account: 值得是用户的真实电话号码**
- **password: 指的是用户的密码**


注册成功消息:

```
{
    "code": 0,
    "msg": "success :) "
}
```

#### 注册失败消息:

- 用户的消息缺失,如
  ```
  {"nickname": "qzxxxx","username": "邱泽鑫","password": "123456"}
  ```
  返回如下:
    ```
    {
    "code": 500311,
    "msg": "用户信息缺失"
	}
    ```
- 已有用户存在返回:
  ```
  {
  "code": -1,
  "msg": "error :( , 电话号码重复."
  }
  ```
- 注册失败:
  ```
  {
  "code": -1,
  "msg": "error :( , 注册失败."
  }
  ```

### 修改用户信息接口

`/user/update`: 修改用户信息接口,以json格式上传,请求头需要携带token:

数据格式如下:
```
{"nickname": "邱泽林啦啦啦"}//也可以同时修改密码
```

更新失败信息:

```
{
    "code": 500410,
    "msg": "更新失败"
}
```
更新成功信息
```
{
    "code": 0,
    "msg": "success :) "
}
```

### 初始化用户头像

`/user/header/init`: 初始化用户头像,以form-data形式传入,Post方式请求,请求头需要携带token

![初始化用户头像.PNG](.\img\初始化用户头像.PNG)


![初始化用户头像参数.PNG](.\img\初始化用户头像参数.PNG)

成功后返回结果:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": "group1/M00/00/00/wKh3gmIPXqqAJo2nAABE-Fvu9RQ141.png"
}
```
失败后的返回结果:
```
{
    "code": 500510,
    "msg": "文件上传失败"
}
```

### 改变用户头像

`/user/header/change`: 更新用户头像,以form-data形式传入,Post方式请求,请求头需要携带token

![更新用户头像.PNG](.\img\更新用户头像.PNG)

![更新用户头像格式.PNG](.\img\更新用户头像格式.PNG)

成功后返回结果:

```
{
    "code": 0,
    "msg": "success :) "
}
```

### 仿微信聊天的好友请求 好友请求处理 好友之间即时聊天 添加之后即时聊天的功能接口

(此部分页面不用你写所以不演示了)

`/user/unsigned`: 查询当前用户未阅读的信息

`/user/make`: 发起好友请求,如果发起请求的好友在线的话，好友会即时收到这条信息。

`/user/friendRequest`: 查询当前用户未处理的好友请求

`/user/friends`: 查询当前用户的好友

`/user/handler`: 处理当前的好友请求，如果通过的话，发起好友请求的用户页面会有你的聊天窗口可以即时的聊天

**好友之间即时聊天的实现**

![好友聊天的即时实现.PNG](.\img\好友聊天的即时实现.PNG)

**启动netty服务器的时机**

![启动netty服务器的时间.PNG](.\img\启动netty服务器的时间.PNG)




### 上传图片接口

`/img/single/upload` : 此接口是单个上传图片的接口

可以用 `form-data`的形式上传图片,也可以使用json格式的数据上传图片,以Post方式请求:

> 请求头应该携带token


![单文件上传.jpg](.\img\单文件上传.jpg)


上传成功后的json数据为:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "imgId": 1,
        "imgOriginName": "fd20081eabf95ef3cbabfbcecc54c7f.png",
        "imgSize": 1820302,
        "imgType": "image/png",
        "groupName": "group1",
        "imgRemotePath": "M00/00/00/wKh3gmIN9baASY6FABvGjtFB2Ig760.png",
        "createTime": null
    }
}
```
### 多图片上传,限制5张

`/img/multi/upload`: 多图片上传接口,以from-data形式上传，或者以json格式数据上传，请求方式为Post:

> 请求头应该携带token


![请求头应携带token.jpg](.\img\请求头应携带token.jpg)

![多文件上传.jpg](.\img\多文件上传.jpg)

上传成功，返回数据:
```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "imgId": 2,
            "imgOriginName": "1645084674(1).png",
            "imgSize": 71550,
            "imgType": "image/png",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiAFyVLAAEXfkW8kqA021.png",
            "createTime": null
        },
        {
            "imgId": 3,
            "imgOriginName": "fa06fbc70841ea6a9b234e3f24146ac.jpg",
            "imgSize": 166669,
            "imgType": "image/jpeg",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiAVlXYAAKLDRtbQqs653.jpg",
            "createTime": null
        },
        {
            "imgId": 4,
            "imgOriginName": "微信图片_20220210161604.jpg",
            "imgSize": 26054,
            "imgType": "image/jpeg",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiACaJ3AABlxnix5so128.jpg",
            "createTime": null
        }
    ]
}
```
### 获取用户的所有图片接口

`/img/user`: 请求头依然需要携带token,其他不用携带参数，以Get方式请求就行:


![获取用户图片.jpg](.\img\获取用户图片.jpg)

成功后的，返回数据:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "imgId": 1,
            "imgOriginName": "fd20081eabf95ef3cbabfbcecc54c7f.png",
            "imgSize": 1820302,
            "imgType": "image/png",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIN9baASY6FABvGjtFB2Ig760.png",
            "createTime": "2022-02-17 07:13:58"
        },
        {
            "imgId": 2,
            "imgOriginName": "1645084674(1).png",
            "imgSize": 71550,
            "imgType": "image/png",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiAFyVLAAEXfkW8kqA021.png",
            "createTime": "2022-02-17 08:22:16"
        },
        {
            "imgId": 3,
            "imgOriginName": "fa06fbc70841ea6a9b234e3f24146ac.jpg",
            "imgSize": 166669,
            "imgType": "image/jpeg",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiAVlXYAAKLDRtbQqs653.jpg",
            "createTime": "2022-02-17 08:22:16"
        },
        {
            "imgId": 4,
            "imgOriginName": "微信图片_20220210161604.jpg",
            "imgSize": 26054,
            "imgType": "image/jpeg",
            "groupName": "group1",
            "imgRemotePath": "M00/00/00/wKh3gmIOBbiACaJ3AABlxnix5so128.jpg",
            "createTime": "2022-02-17 08:22:16"
        }
    ]
}
```


### 下载图片

`/img/download/{imgId}`: 下载图片的接口,以Get方式请求,注意{imgId}里面是你要下载图片的imgId,这在之前你请求图片数据然后展示渲染，数据里面有imgId.也就是说你需要以这个imgId写在url来作为请求,详情请看下图:

![图片url.jpg](.\img\图片url.jpg)


![图片下载请求方式.jpg](.\img\图片下载请求方式.jpg)

成功后返回的数据:

![图片下载.jpg](.\img\图片下载.jpg)

在浏览器的时候会有进度条,以谷歌浏览器为例:

![谷歌.jpg](.\img\谷歌.jpg)

### 视频上传

`/video/single/upload`: 上传视频的接口,此为未用js截取页面当作封面和视频文件一起上传的接口,仅上传视频文件即可:

> 请求头需要携带token


![视频单文件上传.jpg](.\img\视频单文件上传.jpg)


![视频单文件上传格式.jpg](.\img\视频单文件上传格式.jpg)

成功后返回结果:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "videoId": 2,
        "videoOriginName": "第一条视频.mp4",
        "videoCoverType": "png",
        "groupName": "group1",
        "videoCoverRemotePath": "M00/00/00/wKh3gmIOOleAeXQeAAp8-YxhLXI625.png"
    }
}
```

**需要注意的是，后端截取视频帧文件速度极慢,处理一个26M的视频截取第一帧作为封面大约需要2s的时间,而上传视频文件到FastDFS反而很快。所以建议是不使用该接口作为视频上传的接口，此为备用选择。**

### 获取视频文件

`/video/getVideo/{videoId}`: 获取视频文件的接口,需要在url后面加上你要获取视频的videoId,该videoId,再你上传视频文件后，返回的结果中有给出

> 请求头里面需要带有token

![获取视频文件.jpg](.\img\获取视频文件.jpg)

成功的结果数据:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "videoId": 2,
        "videoSize": 1267244,
        "videoType": "video/mp4",
        "groupName": "group1",
        "videoRemotePath": "M00/00/00/wKh3gmIOOleAVKjAABNWLGsKkAk973.mp4",
        "createTime": "2022-02-17 12:06:48"
    }
}
```
### 获取视频文件的详细信息(包括视频文件的封面和视频文件)

`/video/getDetail/{videoId}` : 接口，需要在url后面加上你要获取视频的videoId,该videoId,再你上传视频文件后，返回的结果中有给出

> 请求头里面需要带有token


![获取视频文件的消息信息方式.PNG](.\img\获取视频文件的消息信息方式.PNG)

成功后返回的结果:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "videoId": 2,
        "video": {
            "videoSize": 1267244,
            "videoType": "video/mp4",
            "groupName": "group1",
            "videoRemotePath": "M00/00/00/wKh3gmIOOleAVKjAABNWLGsKkAk973.mp4"
        },
        "videoOriginName": "第一条视频.mp4",
        "videoCoverType": "png",
        "videoCoverRemotePath": "M00/00/00/wKh3gmIOOleAeXQeAAp8-YxhLXI625.png"
    }
}
```

### 获取用户的所有视频文件详细信息

`/video/user/get`: 此方法的接口，使用Get请求方式获得，无需携带参数，只需要请求头携带token:


![获取视频文件的消息信息方式.PNG](.\img\获取视频文件的消息信息方式.PNG)

成功后返回的结果:
```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "videoId": 2,
            "video": {
                "videoSize": 1267244,
                "videoType": "video/mp4",
                "groupName": "group1",
                "videoRemotePath": "M00/00/00/wKh3gmIOOleAVKjAABNWLGsKkAk973.mp4"
            },
            "videoOriginName": "第一条视频.mp4",
            "videoCoverType": "png",
            "videoCoverRemotePath": "M00/00/00/wKh3gmIOOleAeXQeAAp8-YxhLXI625.png"
        }
    ]
}
```

### 下载视频文件

`/video/download/{videoId}`: 方法接口,以Get方式请求,需要在url尾部加入你要获取的视频的videoId，该videoId,再你上传视频文件后，返回的结果中有给出.

> 请求头依然需要携带token

![下载视频文件.PNG](.\img\下载视频文件.PNG)

成功后，以谷歌浏览器为例(与图片文件一致):

![谷歌.jpg](.\img\谷歌.jpg)

### 批量上传视频文件(限制是3个)

`/video/multi/upload`: 批量上传的接口,以Post方式请求,数据以form-dara形式,命名为videos上传。

单个文件的大小不能大于30MB。

> 请求头携带token

![多文件上传方式携带token.PNG](.\img\多文件上传方式携带token.PNG)



![多次上传视频文件方式.PNG](.\img\多次上传视频文件方式.PNG)

成功后返回:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "videoId": 5,
            "videoOriginName": "第一条视频.mp4",
            "videoCoverType": "png",
            "groupName": "group1",
            "videoCoverRemotePath": "M00/00/00/wKh3gmIPQVqAQqQuAAp8-YxhLXI071.png"
        },
        {
            "videoId": 6,
            "videoOriginName": "MySQL Workbench 2021-06-14 18-39-39.mp4",
            "videoCoverType": "png",
            "groupName": "group1",
            "videoCoverRemotePath": "M00/00/00/wKh3gmIPQVuAXH6WAAL6UKTGM3U202.png"
        }
    ]
}
```

### 音乐上传

`/music/single/upload`: 上传单个音乐文件的接口,以form-data方式 命名为music Post方式上传.

自动识别机制: The Chainsmokers - Everybody Hates Me.flac 像这种**歌手名 - 歌曲名 . 后缀名**的歌曲可以自动识别当前歌曲的歌手和歌曲专辑名字。

> 请求头携带token


![单文件上传音乐携带token.PNG](.\img\单文件上传音乐携带token.PNG)


![单文件上传音乐文件.PNG](.\img\单文件上传音乐文件.PNG)

成功后返回结果:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "musicId": 1,
        "singer": "The Chainsmokers ",
        "songName": " Everybody Hates Me",
        "musicOriginName": "The Chainsmokers - Everybody Hates Me.flac",
        "musicType": "audio/x-flac",
        "musicSize": 28481188,
        "groupName": null,
        "musicRemotePath": null,
        "createTime": null
    }
}
```

### 获取音乐文件

`music/get/{musicId}`:  接口，需要在url后面加上你要获取视频的musicId,该musicId,再你上传音乐文件后，返回的结果中有给出

> 请求头携带token


![获取音乐文件.PNG](.\img\获取音乐文件.PNG)

成功后返回结果:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": {
        "musicId": 1,
        "singer": "The Chainsmokers ",
        "songName": " Everybody Hates Me",
        "musicOriginName": "The Chainsmokers - Everybody Hates Me.flac",
        "musicType": "audio/x-flac",
        "musicSize": 28481188,
        "groupName": "group1",
        "musicRemotePath": "M00/00/00/wKh3gmIPRxKADFbrAbKWpCcfYss83.flac",
        "createTime": "2022-02-18 07:13:18"
    }
}
```

### 多文件上传音乐文件(一次限制5个)

`music/multi/upload`: 多文件上传接口,以form-data形式上传，上传的文件同一命名为musics,Post请求方式请求,请求头携带token:


![批量上传音乐.PNG](.\img\批量上传音乐.PNG)


![批量上传音乐格式.PNG](.\img\批量上传音乐格式.PNG)

成功后,返回数据:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "musicId": 6,
            "singer": "周杰伦 ",
            "songName": " 说好不哭",
            "musicOriginName": "周杰伦 - 说好不哭.mp3",
            "musicType": "audio/mpeg",
            "musicSize": 8982950,
            "groupName": null,
            "musicRemotePath": null,
            "createTime": null
        },
        {
            "musicId": 7,
            "singer": "未知歌手",
            "songName": "维斯塔潘领跑珠海赛道",
            "musicOriginName": "维斯塔潘领跑珠海赛道.m4a",
            "musicType": "audio/mp4",
            "musicSize": 194066,
            "groupName": null,
            "musicRemotePath": null,
            "createTime": null
        }
    ]
}
```
### 获取用户的所有音乐文件

`/music/user/get`: 此方法的接口，使用Get请求方式获得，无需携带参数，只需要请求头携带token:


![获取用户所有音乐.PNG](.\img\获取用户所有音乐.PNG)

成功后的数据格式:

```
{
    "code": 0,
    "msg": "success :) ",
    "data": [
        {
            "musicId": 6,
            "singer": "周杰伦 ",
            "songName": " 说好不哭",
            "musicOriginName": "周杰伦 - 说好不哭.mp3",
            "musicType": "audio/mpeg",
            "musicSize": 8982950,
            "groupName": "group1",
            "musicRemotePath": "M00/00/00/wKh3gmIPTRSADgWMAIkRpjEGurg419.mp3",
            "createTime": "2022-02-18 07:38:16"
        },
        {
            "musicId": 7,
            "singer": "未知歌手",
            "songName": "维斯塔潘领跑珠海赛道",
            "musicOriginName": "维斯塔潘领跑珠海赛道.m4a",
            "musicType": "audio/mp4",
            "musicSize": 194066,
            "groupName": "group1",
            "musicRemotePath": "M00/00/00/wKh3gmIPTRSAYDCEAAL2EmCKOuI009.m4a",
            "createTime": "2022-02-18 07:38:16"
        },
        {
            "musicId": 1,
            "singer": "The Chainsmokers ",
            "songName": " Everybody Hates Me",
            "musicOriginName": "The Chainsmokers - Everybody Hates Me.flac",
            "musicType": "audio/x-flac",
            "musicSize": 28481188,
            "groupName": "group1",
            "musicRemotePath": "M00/00/00/wKh3gmIPRxKADFbrAbKWpCcfYss83.flac",
            "createTime": "2022-02-18 07:13:18"
        }
    ]
}
```

### 办公文件的上传与批量上传，下载

这些与音乐文件，图片等一致，所以只提供接口，不演示。

`/document/single/upload`:单独上传办公文件

`/document/getDetail/{docuemntId}`:获取用户的办公文件

`/document/user/get`: 获取用户的所有办公文件

`/document/download/{documentId}`: 下载用户的办公文件

`/document/multi/upload`: 批量上传用户办公文件 (一次请求限制5个)



