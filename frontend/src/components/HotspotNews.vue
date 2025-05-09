<template>
  <div class="hotspot-news">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>热点新闻推荐</span>
          <el-button type="text" @click="fetchHotNews">刷新</el-button>
        </div>
      </template>
      <div v-loading="loading">
        <el-empty v-if="hotNews.length === 0 && !loading" description="暂无热点新闻" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="(item, index) in hotNews"
            :key="index"
            :timestamp="item.publishTime || '未知时间'"
            placement="top"
          >
            <el-card shadow="hover">
              <div class="news-title">{{ item.title || `热点新闻${index+1}` }}</div>
              <div class="news-source">来源: {{ item.source || '未知来源' }}</div>
              <div class="news-tags" v-if="item.tags && item.tags.length > 0">
                <el-tag 
                  v-for="tag in item.tags" 
                  :key="tag" 
                  size="small" 
                  effect="plain"
                  class="tag-item"
                >
                  {{ tag }}
                </el-tag>
              </div>
              <div class="news-actions">
                <el-button 
                  type="primary" 
                  size="small" 
                  plain
                  @click="useAsPrompt(item)"
                >
                  用作提示
                </el-button>
                <el-button 
                  type="success" 
                  size="small" 
                  plain
                  @click="useAsTags(item)"
                  :disabled="!item.tags || item.tags.length === 0"
                >
                  使用标签
                </el-button>
              </div>
            </el-card>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';
import { ElMessage } from 'element-plus';

export default {
  name: 'HotspotNews',
  props: {
    onSelectPrompt: {
      type: Function,
      default: null
    },
    onSelectTags: {
      type: Function,
      default: null
    }
  },
  setup(props) {
    const store = useStore();
    const loading = ref(false);
    const hotNews = ref([]);

    // 辅助方法：格式化AI返回的热点新闻数据
    const formatNewsData = (content) => {
      console.log('开始解析内容:', content);
      
      // 如果已经是格式化好的数组，直接返回
      if (Array.isArray(content) && content.length > 0 && 
          content[0].title && content[0].source) {
        return content;
      }
      
      // 如果是字符串，尝试解析为JSON
      if (typeof content === 'string') {
        try {
          const parsed = JSON.parse(content);
          if (Array.isArray(parsed)) {
            return parsed;
          }
        } catch (e) {
          // 不是JSON，开始尝试其他格式
          console.warn('内容不是有效的JSON，开始解析文本格式', e);
        }
        
        // 解析特定的Markdown格式，比如### 标题
        const sections = content.split(/\n\s*###\s+/);
        if (sections.length > 1) {
          console.log('发现Markdown格式的标题分隔符，尝试解析');
          const news = [];
          // 跳过第一个空元素
          for(let i = 1; i < sections.length; i++) {
            const section = sections[i].trim();
            const firstLineEnd = section.indexOf('\n');
            if (firstLineEnd > 0) {
              const title = section.substring(0, firstLineEnd).trim().replace(/^#+\s+/, '');
              const body = section.substring(firstLineEnd).trim();
              
              // 尝试从正文中提取来源和标签
              let source = '科技日报';
              let tags = [];
              
              // 提取可能的标签
              const tagMatches = body.match(/标签|关键词|主题|领域/g);
              if (tagMatches) {
                tags = title.split(/\s+/).filter(t => t.length > 1 && t.length < 8);
                // 添加一些通用标签
                if (title.includes('人工智能') || body.includes('人工智能') || body.includes('AI')) {
                  tags.push('人工智能');
                }
                if (title.includes('科技') || body.includes('科技')) {
                  tags.push('科技');
                }
              }
              
              // 提取来源
              if (body.includes('来源') || body.includes('出处')) {
                const sourceMatch = body.match(/来源[：:]\s*([^\n]+)/);
                if (sourceMatch) {
                  source = sourceMatch[1].trim();
                }
              }
              
              news.push({
                title,
                source,
                publishTime: new Date().toLocaleDateString(),
                tags: tags.length > 0 ? tags : ['科技', '热点', 'AI推荐']
              });
            }
          }
          
          // 如果成功解析出新闻项，返回结果
          if (news.length > 0) {
            console.log('成功解析出Markdown格式的新闻:', news);
            return news;
          }
        }
        
        // 如果没有找到Markdown格式的标题，尝试解析段落
        // 将长文本拆分为多篇"新闻"
        console.log('尝试将长文本拆分为多篇新闻');
        const paragraphs = content.split(/\n\s*\n/).filter(p => p.trim().length > 0);
        if (paragraphs.length > 0) {
          const news = [];
          for (let i = 0; i < Math.min(paragraphs.length, 5); i++) {
            const para = paragraphs[i].trim();
            // 限制标题长度
            const title = para.length > 50 ? para.substring(0, 50) + '...' : para;
            
            // 为每篇"新闻"生成随机来源和标签
            const sources = ['科技日报', '中国科学报', '科学时报', '新华社科技', '科技前沿'];
            const tagPool = ['科技', '创新', '人工智能', '数字化', '互联网', '大数据', '云计算', '物联网', '智能制造'];
            const randomTags = [];
            for (let j = 0; j < 3; j++) {
              const randomIndex = Math.floor(Math.random() * tagPool.length);
              if (!randomTags.includes(tagPool[randomIndex])) {
                randomTags.push(tagPool[randomIndex]);
              }
            }
            
            news.push({
              title,
              source: sources[Math.floor(Math.random() * sources.length)],
              publishTime: new Date().toLocaleDateString(),
              tags: randomTags
            });
          }
          console.log('成功将长文本拆分为多篇新闻:', news);
          return news;
        }
      }
      
      // 如果所有解析方法都失败，创建一个默认的热点新闻项
      console.log('所有解析方法都失败，创建默认新闻项');
      return [createFallbackNewsItem(content)];
    };

    // 创建一个默认的新闻项
    const createFallbackNewsItem = (text) => {
      // 尝试从文本中提取一些信息作为标题
      let title = '热点新闻推荐';
      const lines = text.split('\n');
      if (lines.length > 0) {
        for (const line of lines) {
          const trimmed = line.trim();
          if (trimmed && trimmed.length < 100) {
            title = trimmed;
            break;
          }
        }
      }
      
      return {
        title,
        source: 'AI生成',
        publishTime: new Date().toLocaleDateString(),
        tags: ['热点', 'AI推荐']
      };
    };

    const fetchHotNews = async () => {
      loading.value = true;
      try {
        // 获取当前登录用户ID
        const currentUser = store.state.auth.user;
        const userId = currentUser?.userId;
        
        // 确保有用户ID
        if (!userId) {
          ElMessage.error('未登录或缺少用户信息');
          loading.value = false;
          return;
        }
        
        // 添加必要的请求头
        const headers = {
          'X-User-ID': userId
        };
        
        // 调试输出
        console.log('开始请求热点新闻数据，用户ID:', userId);
        
        // 发送POST请求到后端AI生成接口
        const requestData = {
          prompt: "请帮我生成5条当前热门科技领域的新闻，每条包含标题、来源(媒体名称)、发布时间以及2-4个相关标签。格式为Markdown，每条新闻使用###分隔。例如：\n\n### 新闻标题\n- 来源: 媒体名称\n- 时间: 2025年4月10日\n- 标签: 人工智能,科技创新",
          maxTokens: 1500,
          temperature: 0.8
        };
        
        console.log('请求参数:', requestData);
        
        const response = await store.state.$http.post('/ai/generate', requestData, { headers });
        
        console.log('AI接口响应数据:', response.data);
        
        // 根据接口响应格式处理数据
        if (response.data && response.data.success) {
          const content = response.data.content;
          console.log('解析前的内容:', content);
          const parsedNews = formatNewsData(content);
          console.log('解析后的新闻数据:', parsedNews);
          hotNews.value = parsedNews;
        } else {
          console.error('获取热点新闻失败', response.data?.message || '未知错误');
          hotNews.value = [];
        }
      } catch (error) {
        console.error('获取热点新闻失败', error);
        ElMessage.error('获取热点新闻失败');
        hotNews.value = [];
      } finally {
        loading.value = false;
      }
    };

    const useAsPrompt = (item) => {
      if (props.onSelectPrompt) {
        props.onSelectPrompt(`请根据热点新闻"${item.title}"撰写一篇相关的报道或分析文章。`);
        ElMessage.success('已添加为提示');
      }
    };

    const useAsTags = (item) => {
      if (props.onSelectTags && item.tags) {
        props.onSelectTags(item.tags);
        ElMessage.success('已添加标签');
      }
    };

    onMounted(fetchHotNews);

    return {
      loading,
      hotNews,
      fetchHotNews,
      useAsPrompt,
      useAsTags
    };
  }
};
</script>

<style scoped>
.hotspot-news {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.news-title {
  font-weight: bold;
  margin-bottom: 8px;
  font-size: 16px;
}
.news-source {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}
.news-tags {
  margin: 10px 0;
}
.tag-item {
  margin-right: 5px;
}
.news-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
}
</style> 