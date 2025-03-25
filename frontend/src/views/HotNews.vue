<template>
  <div class="hot-news-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>AI推荐热点</span>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
        </div>
      </template>

      <el-table :data="hotNewsList" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <div class="news-title">
              <span>{{ row.title }}</span>
              <el-tag
                v-if="row.isNew"
                type="success"
                effect="dark"
                size="small"
                class="new-tag"
              >
                新
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="heat" label="热度" width="200">
          <template #default="{ row }">
            <div class="heat-info">
              <el-progress
                :percentage="row.heat"
                :color="customColors"
                :format="format"
                :stroke-width="15"
              />
              <el-tag
                :type="row.trend === 'up' ? 'danger' : 'info'"
                effect="light"
                size="small"
              >
                <el-icon>
                  <component :is="row.trend === 'up' ? 'ArrowUp' : 'ArrowDown'" />
                </el-icon>
                {{ row.trendValue }}%
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="keywords" label="关键词" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="keyword in row.keywords"
              :key="keyword"
              size="small"
              class="keyword-tag"
            >
              {{ keyword }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleCreateDraft(row)">
              创建稿件
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const hotNewsList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const customColors = [
  { color: '#909399', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#f56c6c', percentage: 60 },
  { color: '#ff3d3d', percentage: 80 },
  { color: '#ff0000', percentage: 100 }
]

// 格式化热度显示
const format = (percentage) => `${percentage}%`

// 获取热点新闻列表
const fetchHotNews = async () => {
  try {
    // TODO: 调用获取热点新闻API
    hotNewsList.value = [
      {
        id: 1,
        title: '人工智能技术突破：新型算法提升效率超50%',
        category: '科技',
        heat: 95,
        trend: 'up',
        trendValue: 15,
        keywords: ['AI', '算法', '效率'],
        updateTime: '2024-01-20 10:30:00',
        isNew: true
      },
      {
        id: 2,
        title: '全球气候变化：极端天气事件频发引发关注',
        category: '环境',
        heat: 88,
        trend: 'up',
        trendValue: 8,
        keywords: ['气候', '极端天气', '环保'],
        updateTime: '2024-01-20 09:45:00',
        isNew: true
      },
      {
        id: 3,
        title: '数字经济发展：新业态带动就业增长',
        category: '经济',
        heat: 75,
        trend: 'down',
        trendValue: 5,
        keywords: ['数字经济', '就业', '发展'],
        updateTime: '2024-01-20 08:30:00',
        isNew: false
      }
    ]
    total.value = 100
  } catch (error) {
    ElMessage.error('获取热点新闻失败')
  }
}

// 刷新列表
const handleRefresh = () => {
  fetchHotNews()
}

// 创建稿件
const handleCreateDraft = (news) => {
  try {
    // TODO: 调用创建稿件API
    router.push({
      path: '/content/edit',
      query: {
        title: news.title,
        category: news.category,
        tags: news.keywords
      }
    })
  } catch (error) {
    ElMessage.error('创建稿件失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchHotNews()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchHotNews()
}

onMounted(() => {
  fetchHotNews()
})
</script>

<style scoped>
.hot-news-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.news-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.new-tag {
  transform: scale(0.8);
}

.heat-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.keyword-tag {
  margin-right: 5px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>