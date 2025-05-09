<template>
  <el-container class="draft-edit">
    <el-main>
      <div class="page-header">
        <h2>{{ isNew ? '创建草稿' : '编辑草稿' }}</h2>
        <el-button type="text" @click="$router.push('/content')">
          返回列表
        </el-button>
      </div>
      <draft-editor />
    </el-main>
  </el-container>
</template>

<script>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import DraftEditor from '@/components/DraftEditor.vue';

export default {
  name: 'DraftEdit',
  components: { DraftEditor },
  setup() {
    const route = useRoute();
    const isNew = computed(() => !route.params.id);

    return {
      isNew
    };
  }
};
</script>

<style scoped>
.draft-edit {
  padding: 20px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
</style> 