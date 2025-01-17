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
- [gotrade](https://github.com/cyanly/gotrade) golang语言的fix 协议，属于POC阶段，目前还不能用于生产
- [fasters](https://lib.rs/crates/fasters) 基于纯Rust的fix引擎
- [quickfixengine](https://quickfixengine.org/) fix引擎开源网站
- [quickfix](https://github.com/quickfix/quickfix) fix C++库
- [quickfixgo](https://github.com/quickfixgo/quickfix) fix Go库
- [quickfixn](https://github.com/connamara/quickfixn) fix NET库
- [goFAST](https://github.com/co11ter/goFAST) FAST Go库，看起来还不够完善，delta等操作符是TODO List
- [QuickFIX Python Samples](https://github.com/rinleit/quickfix-python-samples) QuickFIX  Python示例
- [Financial Information eXchange protocol implemented in Rust](https://ferrumfix.org/) FIX Rust实现
- [fixio](https://github.com/kpavlov/fixio) FIX Protocol Support for Netty
- [simplefix](https://github.com/da4089/simplefix) Simple FIX protocol implementation for Python
- [fixjs](https://github.com/defunctzombie/fixjs) Financial Information Exchange protocol in javascript
- [fix2json](https://github.com/SunGard-Labs/fix2json)A command-line utility to present FIX protocol messages as JSON or YAML
- [hffix](https://github.com/jamesdbrock/hffix) Financial Information Exchange Protocol C++ Library 基于头文件的FIX库
- [API信息](https://help.cqg.com/apihelp/index.html#!Documents/welcometoapihelp1.htm) API客户端调用说明，部分FIX、FAST内容阐述比较细致
- [通过硬件加速FIX/FAST协议解析](http://www.cs.columbia.edu/~sedwards/classes/2013/4840/reports/FIX-FAST.pdf) 
- [Artio](https://github.com/real-logic/artio)   - Resilient High-Performance FIX and FIXP Gateway
- [erlang_fast](https://github.com/dmitryme/erlang_fast) FIX/FAST decode/encode facility
- [fix-rs](https://github.com/jbendig/fix-rs)fix-rs is a FIX (Financial Information Exchange) engine written in Rust.
- [pyfixmsg ](https://github.com/morganstanley/pyfixmsg) pyfixmsg is a library for parsing, manipulating and serialising FIX messages, primarily geared towards testing
- [Spring Boot Starter for QuickFIX/J](https://github.com/esanchezros/quickfixj-spring-boot-starter) This project is a Spring Boot Starter for QuickFIX/J messaging engine for the FIX protocol. It simplifies the configuration required to create and start an Initiator or Acceptor, and handles the lifecycle of the Connector.
# 重点内容
## 解析逻辑
各操作符解析逻辑，可以参照下图：

![解析逻辑](pic/fieldOperatorsCheatSheet.png)
