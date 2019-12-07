# fixfast

通过OpenFast对FAST协议进行深入分析

2019.07 1.0pre版本

# 引言

之前在个人网站、微信公众号写了50篇左右的文章，基于OpenFast对于FIX/FAST协议做了分析。

2018年考虑整理成书，补充一些系统架构、FPGA方面的内容。但种种原因，未能做到。因此对之前的内容做了整理，形成了PDF版本。

考虑到对于OpenFast架构笔墨不算太多，主要围绕FAST协议，通过OpenFast进行分析，因此起名<<深入理解FAST协议>>。

希望通过本书，能够帮助读者理解FAST协议，便于解析沪、深等市场行情。

书本内容还是潜在有不少的问题、BUG，希望能够和大家一起交流、不断完善，可以通过issue、PR进行提交反馈。

# 修订

## 2019.07.21

- 更新了第二章中mini-fix链接；
- 第三章中3.4字节序列，编码为整数2014的，修订为基于1024作为示例。

# FAST/FIX相关网站
## 网站
- [libtrading](https://github.com/libtrading/libtrading) 低延迟交易接口
- [mfast](<https://github.com/objectcomputing/mFAST>) FAST协议解析
- [Philadelphia ](<https://github.com/paritytrading/philadelphia>) 基于JVM的低延迟FIX引擎
- [FIX8](https://www.fix8.org/) 高性能C++ FIX框架
- [onixs](https://www.onixs.biz/) 低延迟FIX引擎产品

- [fix-fast-tutorial](http://jettekfix.com/education/fix-fast-tutorial/) **强烈推荐**

# 重点内容
## 解析逻辑
各操作符解析逻辑，可以参照下图：

![解析逻辑](pic/fieldOperatorsCheatSheet.png)