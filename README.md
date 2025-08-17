# ScanWithDB

一个灵活的Android条码扫描与数据库管理应用，允许用户扫描条码并将其保存到可动态切换的多个本地数据库中。

[![许可证: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

---

## 关于项目

ScanWithDB 旨在提供一个简单而强大的工具，用于需要将扫描数据分类存储在不同数据库的场景。用户可以轻松创建新的数据库文件，在不同的数据库之间切换，以及管理其中的数据。本项目完全开源，并使用现代化的Android开发技术栈构建。

### 主要技术栈

*   **Kotlin**: 主要开发语言
*   **Android Jetpack**: 
    *   **Room**: 用于本地数据库存储
    *   **Navigation Component**: 用于处理应用内的Fragment导航
    *   **ViewModel & LiveData/StateFlow**: 用于UI状态管理和响应式编程
    *   **CameraX**: 用于实现相机功能
    *   **SplashScreen API**: 用于实现Android 12+的现代化启动画面
*   **Hilt**: 用于依赖注入
*   **Google ML Kit**: 用于条码和二维码扫描
*   **Material Design 3**: 用于UI组件和设计

## 主要功能

*   **条码/二维码扫描**: 使用设备摄像头快速扫描各种条码。
*   **多数据库管理**: 
    *   创建新的数据库文件。
    *   在已有的数据库之间自由切换。
    *   删除不再需要的数据库。
*   **数据管理**: 
    *   查看当前数据库中的所有扫描记录。
    *   标记或取消标记特定条目。
    *   清空当前数据库的所有数据。
*   **自定义启动画面**: 包含作者信息的自定义启动动画。
*   **关于页面**: 提供详细的开源许可和作者信息。

## 开始使用

你可以通过以下步骤在本地构建和运行此项目：

1.  **克隆仓库**
    ```sh
    git clone https://github.com/CoWave-Fall/ScanWithDB.git
    ```
2.  **打开项目**
    使用最新稳定版的Android Studio打开项目。
3.  **构建项目**
    等待Android Studio完成Gradle同步和项目索引，然后点击 `Run 'app'` 按钮进行构建和安装。

## 许可证

本项目采用 **GNU General Public License v3.0** 进行许可。详情请参阅 `LICENSE` 文件。

## 作者

*   **代码**: Gemini, CoWave-Fall
*   **美术**: CoWave-Fall
