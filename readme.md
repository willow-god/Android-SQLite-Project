# Android SQLite 实验项目

## 项目简介
本项目是一个Android应用程序，旨在演示SQLite数据库在Android平台上的实践应用。用户可以通过该应用程序进行用户信息的增加、删除、修改和查询操作，深入了解SQLite数据库的使用方法。

## 功能概述

### 用户信息输入
- 用户可以通过图形用户界面（GUI）输入包括姓名、密码、电话、邮箱和性别在内的用户信息。

### 数据存储
- 输入的用户信息将被存储在SQLite数据库中，确保数据的持久化。

### 数据展示
- 用户可以通过界面上的下拉列表选择一个用户，应用程序将展示该用户的详细信息。

### 数据操作
- 提供了添加、保存、删除和清空等操作按钮，用户可以对用户信息进行相应操作。

## 环境要求

### 软件环境
- Android Studio 4.2 或更高版本，用于开发和构建Android应用程序。
- Android SDK 30 或更高版本，提供API支持。
- Java Development Kit (JDK) 1.8 或更高版本，提供Java开发环境。

### 硬件环境
- 一台可以运行Android Studio的计算机。
- 至少一个Android设备或模拟器实例，用于测试应用程序。

## 安装指南

### 克隆项目
使用Git克隆项目到本地机器：
```bash
git clone https://github.com/willow-god/Android-SQLite-Project.git
```

### 导入项目
- 打开Android Studio，选择“Open an existing Android Studio project”。
- 导航到克隆的项目文件夹并打开。

### 配置模拟器
- 如果没有连接实际设备，配置Android Virtual Device (AVD) 管理器以创建模拟器实例。

### 构建和运行
- 点击IDE中的运行按钮（绿色三角形）来安装并启动应用程序。

## 使用说明

### 主界面
![主界面](./img/home.png)

### 添加用户信息
1. 打开应用程序，填写必要的用户信息字段。
2. 点击“添加”按钮，应用程序将用户信息添加到数据库中。

### 展示用户信息
1. 在下拉列表中选择一个用户名。
2. 应用程序自动填充界面中的输入框，展示用户详细信息。

### 修改用户信息
1. 修改界面中的用户信息字段。
2. 点击“保存”按钮，应用程序将更新数据库中的用户信息。

### 删除用户信息
1. 在下拉列表中选择一个用户。
2. 点击“删除”按钮，应用程序将从数据库中删除该用户的所有信息。

### 清空界面或数据库
![清空弹窗](./img/clean.png)

1. 点击“清空”按钮。
2. 选择“清空数据库”以删除数据库中的所有用户信息，或选择“清空界面”以清除界面上的输入框内容。

## 数据库设计

### 数据库
- 名称：`userDatabase`
- 版本：1

### 表
- 名称：`users`
- 列：
  - `id`：INTEGER，主键，自增。
  - `username`：TEXT，用户名，唯一。
  - `password`：TEXT，密码。
  - `phone`：TEXT，电话号码。
  - `email`：TEXT，邮箱地址。
  - `gender`：TEXT，性别。

## 技术实现

### 主要类和接口
- `DatabaseHelper`：继承自`SQLiteOpenHelper`，用于创建和升级数据库。
- `SQLiteDatabase`：用于对数据库执行读写操作。
- `ContentValues`：用于向数据库中插入或更新数据。
- `Cursor`：用于从数据库中检索数据。
- `AlertDialog.Builder`：用于创建和管理对话框。

### 关键代码示例

#### 创建数据库表
```java
public class DatabaseHelper extends SQLiteOpenHelper {
    // 数据库版本、表名、列名等常量...

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }
}
```

#### 插入新用户
```java
public long addUser(SQLiteDatabase db, String username, String password, String phone, String email, String gender) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_USERNAME, username);
    values.put(COLUMN_PASSWORD, password);
    values.put(COLUMN_PHONE, phone);
    values.put(COLUMN_EMAIL, email);
    values.put(COLUMN_GENDER, gender);
    return db.insert(TABLE_USERS, null, values);
}
```

#### 删除用户
```java
public int deleteUser(SQLiteDatabase db, String username) {
    String selection = COLUMN_USERNAME + "=?";
    String[] selectionArgs = new String[]{username};
    return db.delete(TABLE_USERS, selection, selectionArgs);
}
```

### 布局文件
- 应用程序的布局文件使用XML格式定义，包含用户界面的所有元素。

## 贡献者

### 主要贡献者
- [清羽飞扬](https://blog.qyliu.top/about)：负责数据库设计和实现。

### 贡献指南
我们欢迎任何形式的贡献

## 许可

### 开源许可
本项目采用[CC BY-NC-SA 4.0](https://creativecommons.org/licenses/by-nc-sa/4.0/?ref=chooser-v1)许可，请在分发或使用时遵守相关协议。

## 问题与支持

### 问题跟踪
如果您在使用过程中遇到任何问题或有功能上的建议，请提交issue到我们的[问题跟踪器](https://github.com/willow-god/Android-SQLite-Project/issues)。

### 联系邮箱
对于非公开问题，您可以通过以下邮箱联系我们：[3162475700@qq.com](mailto:3162475700@qq.com)。

## 致谢

我们感谢所有为本项目提供帮助和支持的人，包括但不限于：

- Android开发社区
- SQLite数据库团队
- Android Studio开发团队

## 附录

### 版本历史
- v1.0 - 2024-05-26：项目初始化，基本功能实现。