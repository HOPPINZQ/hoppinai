### 不要吐槽我的表结构设计，因为这是测试表，我把所有字段堆在一起了

#### ddl.sql是表结构，先执行这个sql脚本，data.sql是数据

| 表名 | 用途 | 是否关键 | 是否带着数据 |
| --- | --- | -------- | --------- |
| apifox_ai_doc | llm文档表，内容是我从Apifox的apihub里爬的 | ❌ | ✅ |
| blog | 博客表，带着数据，仅用来为博客的搜索引擎搜索和智能搜索做演示用 | ❌ | ✅ |
| video | 视频表，同上，也是演示用 | ❌ | ✅ |
| embedding | 向量表，也是演示用，存储视频和博客所有数据的向量，向量大模型是text-embedding-ada-002 | ❌ | ✅ |
| chat | 聊天栏目表，对话会产生数据 | ✅ | ❌ |
| chatmessage | 具体聊天内容 | ✅ | ❌ |
| chatfunction | 聊天函数表 | ❌ | ❌ |
| csgo_match_copy | csgo数据表，包含了我2024所有csgo战绩，用来演示智能问数和MCP用 | ❌ | ✅ |
| gptsetting | 大模型配置表，自己在页面新增就行 | ✅ | ❌ |
| gptmodel | 保存上面gptsetting表里配置的地址支持，或可用的大模型 | ✅ | ❌ |
| knowledge | 知识库表 | ❌ | ❌ |
| knowledge_attr | 知识库属性表 | ❌ | ❌ |
| knowledge_attr_label | 知识库标签表 | ❌ | ❌ |
| knowledge_doc | 知识库的文档表 | ❌ | ❌ |
| knowledge_qa | 知识库问答对表 | ❌ | ❌ |
| prompt | 提示词表，带着数据，是我收集的 | ❌ | ✅ |
| public_mcp | mcp表，带着数据，是我爬的 | ❌ | ✅ |
| public_mcp_type | mcp类型表，也带着数据 | ❌ | ✅ |
| user | 用户表，不带数据，用手机号登录就行，不存在就创建 | ✅ | ❌ |
