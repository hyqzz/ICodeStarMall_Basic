<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <van-nav-bar
    :title="title"
    left-arrow
    @click-left="onClickLeft"
    class="common-nav-bar"
  />
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

interface Props {
  title?: string
}

const props = withDefaults(defineProps<Props>(), {
  title: ''
})

const router = useRouter()

const onClickLeft = () => {
  router.back()
}
</script>

<style scoped>
.common-nav-bar {
  position: sticky;
  top: 0;
  z-index: 100;
}
</style> 