<template>
  <div class="editor-container">
    <Toolbar
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
      style="border-bottom: 1px solid #ccc"
    />
    <Editor
      :defaultConfig="editorConfig"
      :mode="mode"
      v-model="valueHtml"
      @onCreated="handleCreated"
      @onChange="handleChange"
      style="height: 500px; overflow-y: hidden;"
    />
  </div>
</template>

<script>
import { ref, shallowRef, onBeforeUnmount, watch } from 'vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

export default {
  name: 'RichTextEditor',
  components: {
    Editor,
    Toolbar
  },
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    mode: {
      type: String,
      default: 'default'
    }
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    // 编辑器实例，必须用 shallowRef
    const editorRef = shallowRef()

    // 内容 HTML
    const valueHtml = ref(props.modelValue)

    // 模拟 ajax 异步获取内容
    watch(() => props.modelValue, (val) => {
      valueHtml.value = val
    })

    // 工具栏配置
    const toolbarConfig = {
      excludeKeys: [
        'uploadImage',
        'uploadVideo',
        'insertTable',
        'group-video',
        'group-image'
      ]
    }

    // 编辑器配置
    const editorConfig = {
      placeholder: '请输入内容...',
      autoFocus: false,
      readOnly: props.mode === 'preview'
    }

    // 组件销毁时，也及时销毁编辑器
    onBeforeUnmount(() => {
      const editor = editorRef.value
      if (editor == null) return
      editor.destroy()
    })

    const handleCreated = (editor) => {
      editorRef.value = editor
    }

    const handleChange = (editor) => {
      emit('update:modelValue', editor.getHtml())
      emit('change', editor.getHtml())
    }

    return {
      editorRef,
      valueHtml,
      toolbarConfig,
      editorConfig,
      handleCreated,
      handleChange
    }
  }
}
</script>

<style scoped>
.editor-container {
  border: 1px solid #ccc;
  z-index: 100;
}
</style>