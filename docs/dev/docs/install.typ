#import "/lib/lib.typ": *;
#let title = [安装];
#show: template.with(title: title);

/ #[= 本地部署]:

   要想在本地构建并浏览此文档，您首先需要安装Typst环境

   / Windows:

      winget：以管理员运行powershell，然后执行命令：

      ```powershell
      winget install typst
      ```

   / Linux:

      首先安装Cargo
      
      ```bash
      curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
      ```

      然后使用Cargo构建Typst cli

      ```bash
      cargo install --locked typst-cli
      ```

   #cardInfo[
      更多安装方式详见
      #link("https://typst.app/open-source/#download")[Typst官网]
      或
      #link("https://github.com/typst/typst?tab=readme-ov-file#installation")[Github仓库]
   ];

   克隆本项目（文档）并使用gradle构建

   / Windows:

      ```cmd
      .\gradlew.bat serveDocs
      ```

   / Linux:

      ```bash
      ./gradlew serveDocs
      ```

   这将编译所有文档并开启一个本地的http服务

/ #[= 本地编辑]:

   克隆项目后，在本地进行编辑

   这里以VSCode为例

   #cardInfo[
      (Idea) Jet Brains内的插件目前是不完善的，缺少必要功能，请先使用VSCode
   ];

   在VSCode内安装"Tinymist Typst"插件，然后用VSCode打开项目的文档文件夹(/docs/)

   #cardAttention[
      不要用VSCode打开整个项目文件夹，必须打开文档的根目录
   ];

   此时你可以开始编辑文档中每个页面了，补全等功能应当是可用的

   #cardTip[
      使用"Ctrl+K+V"打开预览面板，点击预览中的文字可以跳转到代码
      
      此插件的实时预览仅支持pdf格式，这与目标的html有很大差异，仅供参考
   ];

   完成编辑后您应当本地部署以查看效果