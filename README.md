# txtazw3

### 给TXT小说章节自动加上MarkDown段落标记，支持多级目录

#### 匹配优先级

- 卷
- 部
- 章
- 节

~~~
* 若同时出现以上章节类型，优先级靠后的作为子目录

* 删除空行和行首空格，方便后续转换Kindle电子书

* 自动检测原始文件编码，并统一输出UTF-8编码的TXT文件

* 日志输出目录检测结果，章节不连续的有标识，可到小说官网对比，确认是作者本来就写错了，还是TXT的章节有重复或者缺失
~~~


# Calibre

#### 生成目录

~~~
一级目录(XPath 表达式)
//h:h1

二级目录(XPath 表达式)
//h:h2
~~~

#### 布局设置

~~~
删除段间空行

在段落间插入空白行
~~~
